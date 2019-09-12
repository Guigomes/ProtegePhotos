package ggsoftware.com.br.protegephotospro.activitys;


import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ggsoftware.com.br.protegephotospro.Constantes;
import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.dao.ImageDAO;
import ggsoftware.com.br.protegephotospro.dao.ImagemVO;
import ggsoftware.com.br.protegephotospro.dao.PastaDAO;
import ggsoftware.com.br.protegephotospro.dao.PastaVO;
import ggsoftware.com.br.protegephotospro.entidades.Foto;
import ggsoftware.com.br.protegephotospro.utils.ImageSaver;
import ggsoftware.com.br.protegephotospro.utils.Utils;

import static ggsoftware.com.br.protegephotospro.Constantes.ALTERAR_SENHA;

public class GaleriaActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private static final int PICK_IMAGE = 1;
    PastaVO pastaSelecionada;
    List<ImagemVO> listaImagens;
    ImageDAO imagemDAO;
    SalvarImagens2 salvadorImagems;

    private List<Foto> mFotos;
    private Context mContext;


    RelativeLayout root;
    int quantidadeArquivos = 0;
    ArrayList<ImageGalleryAdapter.MyViewHolder> views = new ArrayList<>();
    boolean modoSelecao = false;
    ImageGalleryAdapter adapter;
    TextView textViewDialog;
    ProgressBar progressBar;

    ProgressBar progressBarCircular;

    int progress;
    PastaDAO pastaDAO;
    private long downloadID;

    androidx.appcompat.app.AlertDialog dialog;
     boolean isNovaPastaVisivel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        pastaDAO = new PastaDAO(GaleriaActivity.this);
        progressBarCircular = (findViewById(R.id.progressBarCircular));

        Bundle extras = getIntent().getExtras();
     //    root  =(RelativeLayout) findViewById(R.id.galeria_root);
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        String nomePasta = null;
        if (extras != null) {

            nomePasta = (String) extras.get("nomePasta");
        }
        if (nomePasta != null) {
            pastaSelecionada = pastaDAO.buscarPorNome(nomePasta);

        } else {

            Toast.makeText(getApplicationContext(), "Erro ao encontrar pasta", Toast.LENGTH_LONG).show();

        }

        setTitle(pastaSelecionada.getNomePasta());



        listarPatas();

    }

    public  void listarPatas(){
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        imagemDAO = new ImageDAO(GaleriaActivity.this);


        listaImagens = imagemDAO.listarPorPasta(pastaSelecionada.getNomePasta());

        if (listaImagens.size() == 0) {
            ((Button) findViewById(R.id.btn_novas_fotos)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.VISIBLE);
        } else {
            ((Button) findViewById(R.id.btn_novas_fotos)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.GONE);
        }
        adapter = new ImageGalleryAdapter(this, Foto.getSpacePhotos(listaImagens));
        recyclerView.setAdapter(adapter);

        registerForContextMenu(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.menu_galeria, menu);


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        listarPatas();
    }

    public void upload(View v){
        android.app.AlertDialog.Builder dialogPrimeiroUpload =  new android.app.AlertDialog.Builder(new ContextThemeWrapper(GaleriaActivity.this, R.style.Dialog));

        dialogPrimeiroUpload.setTitle(R.string.msg_title_proteger_fotos)
                .setMessage(getString(R.string.msg_proteger_fotos))
                .setPositiveButton(getString(R.string.btn_entendi), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addImagem(null);
                    }
                }).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_download_imagem:


                abrirDialogExportarFotos();


                break;
            case R.id.action_selecionar_todos:
                selecionarTodos();
                break;
            case R.id.action_excluir_pasta:
                abrirDialogExcluirPasta();

                break;
            case R.id.action_add_imagem:
                addImagem(null);
                break;
            case R.id.action_excluir_imagem:
                abrirDialogExcluirFotos();

                break;
            case R.id.action_cancelar_selecao:
                cancelarSelecao();
                break;
            case R.id.action_new_folder:
                criarNovaPasta();
                break;

            case R.id.action_share:
                compartilharImagem();

                break;
            case R.id.action_configurar_pasta:
                abrirEditarPasta();

                break;


            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void selecionarTodos() {
        for (Foto foto : mFotos) {
            foto.setSelected(1);
        }

        int count = views.size();
        for (int i = 0; i < count; i++) {
            ImageGalleryAdapter.MyViewHolder v;
            v = views.get(i);

            //call imageview from the viewholder object by the variable name used to instatiate it
            ImageView imageView1 = v.mPhotoImageView;
            ImageView imageView3 = v.mImageCheck;
            imageView3.setVisibility(View.VISIBLE);
            imageView1.setPadding(30, 30, 30, 30);

        }
        modoSelecao = true;
        GaleriaActivity.this.invalidateOptionsMenu();
    }

    private void abrirEditarPasta() {
        final AlertDialog dialog;

        final View alertDialogView = LayoutInflater.from(GaleriaActivity.this).inflate
                (R.layout.dialog_configurar_pasta, null);


        final EditText edtNomeAlterarPasta = alertDialogView.findViewById(R.id.edtNomeAlterarPasta);

        final RadioButton rdbVisivel = alertDialogView.findViewById(R.id.btn_rdb_visivel);

        RadioButton rdbInvisivel = alertDialogView.findViewById(R.id.btn_rdb_invisivel);


        if(pastaSelecionada.getInvisivel() == 0){
            rdbVisivel.setChecked(true);
        }else{
            rdbInvisivel.setChecked(true);
        }
        edtNomeAlterarPasta.setText(pastaSelecionada.getNomePasta());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(GaleriaActivity.this)
                .setView(alertDialogView)
                .setTitle(getString(R.string.title_alterar_dados_pasta))
                .setPositiveButton(R.string.btn_alterar_dados_pasta, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        boolean pastaAlterada = false;


                        String novoNomePasta = edtNomeAlterarPasta.getText().toString();

                        if(!novoNomePasta.equalsIgnoreCase(pastaSelecionada.getNomePasta())){
                            pastaSelecionada.setNomePasta(novoNomePasta);
                            pastaAlterada = true;
                        }

                        boolean novoIsVisivel = rdbVisivel.isChecked();

                        boolean isVisivel = pastaSelecionada.getInvisivel() == 0;

                        if(!isVisivel == novoIsVisivel){
                            pastaSelecionada.setInvisivel(novoIsVisivel ? 0 : 1);
                            pastaAlterada = true;
                        }



                        if(pastaAlterada) {
                            pastaDAO.updatePasta(pastaSelecionada);

                            setTitle(pastaSelecionada.getNomePasta());

                            Utils.notificar(recyclerView, getString(R.string.msg_sucesso_alterar_nome_pasta));
                        }

                    }
                }).setNeutralButton(R.string.btn_alterar_senha, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alterarSenhaPasta();
                    }
                })

                .setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }



    private void downloadImage() throws FileNotFoundException {

        boolean salvo = false;
        List<File> files = getFilesSelected();
        for (File file :
                files) {
             salvo = Utils.writeFileExternalStorage(GaleriaActivity.this, file);

            if(!salvo){
                break;
            }
        }

        cancelarSelecao();
        if(salvo) {
            Utils.notificar(recyclerView,getString(R.string.msg_download_sucesso_2, getString(R.string.app_name)));

        }else{
            Utils.notificar(recyclerView, getString(R.string.msg_download_erro));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(GaleriaActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private List<File> getFilesSelected() {
        int count = adapter.getItemCount();

        List<File> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {

            Foto foto = mFotos.get(i);
            if (foto.getSelected() == 1) {

                File file = new ImageSaver(GaleriaActivity.this).loadFile(foto.getTitle());

                files.add(file);

            }


        }
        return files;
    }





    private void excluirImagem() {
        int count = adapter.getItemCount();
        List<Foto> fotosAremover = new ArrayList<>();
        for (int i = 0; i < count; i++) {

            Foto foto = mFotos.get(i);

            if (foto.getSelected() == 1) {

                fotosAremover.add(foto);
                new ImageDAO(GaleriaActivity.this).excluir(foto.getId());
            }


        }
        mFotos.removeAll(fotosAremover);


        ImageGalleryAdapter adapter = new ImageGalleryAdapter(this, mFotos);
        recyclerView.setAdapter(adapter);
        cancelarSelecao();
Utils.notificar(recyclerView, getString(R.string.msg_excluir_fotos_sucesso));
        if(mFotos.isEmpty()){

                    ((Button) findViewById(R.id.btn_novas_fotos)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.VISIBLE);
        }

    }

    private void compartilharImagem() {

        List<File> files = getFilesSelected();

        share(files);



    }



    private void criarNovaPasta() {
        final androidx.appcompat.app.AlertDialog dialog;

        final View alertDialogView = LayoutInflater.from(GaleriaActivity.this).inflate
                (R.layout.dialog_nova_pasta, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(GaleriaActivity.this)
                .setTitle(getString(R.string.title_criar_nova_pasta))
                .setView(alertDialogView)
                .setPositiveButton(R.string.btn_criar_pasta, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String    nomePasta = ((EditText) alertDialogView.findViewById(R.id.edtNomeNovaPasta)).getText().toString();


                        if (nomePasta != null && !nomePasta.isEmpty()) {
                            Intent it = new Intent(GaleriaActivity.this, EscolherPadraoActivity.class);

                            it.putExtra("idPasta", -1);
                            it.putExtra("nomePasta", nomePasta);
                            it.putExtra("isPastaVisivel", isNovaPastaVisivel);


                            startActivity(it);
                            dialog.dismiss();
                        } else {
                            Utils.notificar(recyclerView,getString( R.string.msg_erro_criar_pasta_sem_nome) );

                        }
                    }
                })
                .setNeutralButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        dialog = builder.create();

        dialog.show();


    }
        private void alterarSenhaPasta() {
        Intent it = new Intent(GaleriaActivity.this,
                EscolherPadraoActivity.class);

        int idPasta = pastaSelecionada.getId();
        String nomePasta = pastaSelecionada.getNomePasta();

        it.putExtra("idPasta", idPasta);

        it.putExtra("nomePasta", nomePasta);

        it.putExtra("isAlterarSenha", true);
        startActivityForResult(it, ALTERAR_SENHA);


    }

    private void abrirDialogExcluirPasta(){
        android.app.AlertDialog.Builder dialogExcluir =  new android.app.AlertDialog.Builder(new ContextThemeWrapper(GaleriaActivity.this, R.style.Dialog));

        dialogExcluir.setTitle(R.string.msg_title_excluir_pasta)
        .setMessage(getString(R.string.msg_excluir_pasta, pastaSelecionada.getNomePasta()))
        .setPositiveButton(getString(R.string.btn_excluir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
excluirPasta();
            }
        }).setNegativeButton(getString(R.string.btn_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        }).show();
    }


    private void abrirDialogExcluirFotos(){
        android.app.AlertDialog.Builder dialogExcluir =  new android.app.AlertDialog.Builder(new ContextThemeWrapper(GaleriaActivity.this, R.style.Dialog));

        String mensagemExcluirFotos = "";
        if(getFilesSelected().size() == 1){
            String nomeFotoSelecionada = "";
            for (Foto foto : mFotos) {

                if (foto.getSelected() == 1) {
                    nomeFotoSelecionada = foto.getTitle();
                    break;
                }

            }
            mensagemExcluirFotos = getString(R.string.msg_excluir_foto, nomeFotoSelecionada );
        }else{
            mensagemExcluirFotos = getString(R.string.msg_excluir_fotos, String.valueOf(getFilesSelected().size()));
        }
        dialogExcluir.setTitle(R.string.msg_title_excluir_fotos)
                .setMessage(mensagemExcluirFotos)
                .setPositiveButton(getString(R.string.btn_excluir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        excluirImagem();
                    }
                }).setNegativeButton(getString(R.string.btn_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        }).show();
    }

    private void abrirDialogExportarFotos(){
        android.app.AlertDialog.Builder dialogExcluir =  new android.app.AlertDialog.Builder(new ContextThemeWrapper(GaleriaActivity.this, R.style.Dialog));

        String  mensagemExportarFotos = "";
int sizeFilesSelected = getFilesSelected().size();
if(sizeFilesSelected == 0){
    Utils.notificar(recyclerView, getString(R.string.msg_arquivos_nao_selecionados));

}
    else    if(sizeFilesSelected == 1){
            String nomeFotoSelecionada = "";
            for (Foto foto : mFotos) {

                if (foto.getSelected() == 1) {
                    nomeFotoSelecionada = foto.getTitle();
                    break;
                }

            }
            mensagemExportarFotos = getString(R.string.msg_exportar_foto,nomeFotoSelecionada);
            dialogExcluir.setTitle(R.string.msg_title_exportar_foto);
        }else{
            mensagemExportarFotos = getString(R.string.msg_exportar_fotos,String.valueOf(getFilesSelected().size()));;
            dialogExcluir.setTitle(R.string.msg_title_exportar_fotos);
        }



        dialogExcluir.setMessage(mensagemExportarFotos)
                .setPositiveButton(getString(R.string.btn_exportar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            downloadImage();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton(getString(R.string.btn_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        }).show();
    }
    private void excluirPasta() {
        PastaDAO pastaDAO = new PastaDAO(GaleriaActivity.this);

        pastaDAO.excluir(pastaSelecionada.getId());





        startActivity(new Intent(GaleriaActivity.this, MainActivity.class));
        Toast.makeText(GaleriaActivity.this, getString(R.string.msg_sucesso_deletar_pasta), Toast.LENGTH_SHORT).show();

    }

    private void cancelarSelecao() {

        for (Foto foto : mFotos) {
            foto.setSelected(0);
        }

        int count = views.size();
        for (int i = 0; i < count; i++) {
            ImageGalleryAdapter.MyViewHolder v;
            v = views.get(i);

            //call imageview from the viewholder object by the variable name used to instatiate it
            ImageView imageView1 = v.mPhotoImageView;
            ImageView imageView3 = v.mImageCheck;
            imageView3.setVisibility(View.INVISIBLE);
            imageView1.setPadding(0, 0, 0, 0);

        }
        modoSelecao = false;
        GaleriaActivity.this.invalidateOptionsMenu();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (modoSelecao) {
            menu.findItem(R.id.action_excluir_imagem).setVisible(true);
            menu.findItem(R.id.action_cancelar_selecao).setVisible(true);

            menu.findItem(R.id.action_add_imagem).setVisible(false);
            menu.findItem(R.id.action_excluir_pasta).setVisible(false);
            menu.findItem(R.id.action_selecionar_todos).setVisible(true);

            menu.findItem(R.id.action_configurar_pasta).setVisible(false);


            menu.findItem(R.id.action_new_folder).setVisible(false);
            menu.findItem(R.id.action_download_imagem).setVisible(true);
            menu.findItem(R.id.action_share).setVisible(true);
        } else {
            menu.findItem(R.id.action_selecionar_todos).setVisible(false);
            menu.findItem(R.id.action_excluir_pasta).setVisible(true);
            menu.findItem(R.id.action_configurar_pasta).setVisible(true);
            menu.findItem(R.id.action_excluir_imagem).setVisible(false);
            menu.findItem(R.id.action_cancelar_selecao).setVisible(false);
            menu.findItem(R.id.action_add_imagem).setVisible(true);

            menu.findItem(R.id.action_new_folder).setVisible(true);
            menu.findItem(R.id.action_download_imagem).setVisible(false);
            menu.findItem(R.id.action_share).setVisible(false);
        }
        return true;
    }


    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }


    public void addImagem(View v) {
        salvadorImagems = new SalvarImagens2();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {


            case PICK_IMAGE:
                if (imageReturnedIntent != null) {
                    ClipData clipData = imageReturnedIntent.getClipData();
                    if (clipData != null) {

                        progress = 0;
                        showProgressDialog(clipData.getItemCount());
                        salvadorImagems.execute(clipData);
                    } else {
                        Uri selectedImage = imageReturnedIntent.getData();
                        progressBarCircular.setVisibility(View.VISIBLE);
                        new SalvarImagem().execute(selectedImage);
                    }
                }
                break;
            case Constantes.ALTERAR_SENHA:

                Bundle extras = imageReturnedIntent.getExtras();

                String nomePasta = null;
                String pattern = null;
                if (extras != null) {
                    nomePasta = extras.getString("nomePasta");
                    pattern = extras.getString("pattern");

                }


                PastaDAO pastaDAO = new PastaDAO(GaleriaActivity.this);
                PastaVO pastaVO = pastaDAO.buscarPorNome(nomePasta);
                if (pastaVO != null) {
                    pastaVO.setSenhaPasta(pattern);

                    boolean sucesso = pastaDAO.updatePasta(pastaVO);


                    if (sucesso) {
                        Utils.notificar(recyclerView,  getString(R.string.msg_sucesso_alterar_senha));
                        Intent it = new Intent(GaleriaActivity.this, GaleriaActivity.class);
                        it.putExtra("nomePasta", nomePasta);
                        startActivity(it);
                        finish();

                    } else {

                        Utils.notificar(recyclerView, getString(R.string.msg_erro_criar_pasta));

                    }
                } else {
                    Utils.notificar(recyclerView, getString(R.string.msg_pasta_nao_existe));


                }
        }


    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rdb_visivel:
                if (checked)
                    isNovaPastaVisivel = true;

                break;
            case R.id.rdb_invisivel:
                if (checked)
                    isNovaPastaVisivel = false;

                break;
        }
    }
    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }


    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder> {

        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View photoView = inflater.inflate(R.layout.custom_item, parent, false);
            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

            Foto foto = mFotos.get(position);

            ImageView imageView = holder.mPhotoImageView;

            ImageSaver imageSaver = new ImageSaver(GaleriaActivity.this);
            File file = imageSaver.loadFile(foto.getTitle());

            holder.itemView.setLongClickable(true);

            foto.setSelected(0);
            foto.setFilepath(file.getAbsolutePath());
            views.add(holder);

            Glide.with(mContext)
                    .load(file.getAbsolutePath())
                    .asBitmap()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        }

        @Override
        public int getItemCount() {
            return (mFotos.size());
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            public ImageView mImageCheck;

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
                mImageCheck = (ImageView) itemView.findViewById(R.id.imgCheck);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }


            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    Foto foto = mFotos.get(position);

                    if (modoSelecao) {
                        ImageView imagem = (ImageView) view.findViewById(R.id.iv_photo);
                        ImageView imgCheck = (ImageView) view.findViewById(R.id.imgCheck);

                        if (foto.getSelected() == 0) {
                            imagem.setPadding(30, 30, 30, 30);
                            imgCheck.setVisibility(View.VISIBLE);
                            foto.setSelected(1);
                        } else {
                            imagem.setPadding(0, 0, 0, 0);
                            imgCheck.setVisibility(View.INVISIBLE);
                            foto.setSelected(0);

                            boolean encontrou = false;
                            for (Foto foto1 : mFotos) {

                                if (foto1.getSelected() == 1) {
                                    encontrou = true;
                                    break;
                                }

                            }
                            if (!encontrou) {

                                modoSelecao = false;
                                GaleriaActivity.this.invalidateOptionsMenu();

                            }

                        }


                    } else {


                        Intent intent = new Intent(mContext, FotoActivity.class);
                        intent.putExtra(FotoActivity.EXTRA_SPACE_PHOTO, foto);
                        startActivity(intent);


                    }
                }
            }

            @Override
            public boolean onLongClick(View view) {
                int position = getAdapterPosition();


                if (position != RecyclerView.NO_POSITION) {
                    Foto foto = mFotos.get(position);


                    ImageView imagem = (ImageView) view.findViewById(R.id.iv_photo);
                    ImageView imgCheck = (ImageView) view.findViewById(R.id.imgCheck);
                    foto.setSelected(1);

                    imagem.setPadding(30, 30, 30, 30);


                    imgCheck.setVisibility(View.VISIBLE);

                    modoSelecao = true;

                    GaleriaActivity.this.invalidateOptionsMenu();
                }

                return true;
            }


        }

        public ImageGalleryAdapter(Context context, List<Foto> fotos) {
            mContext = context;
            mFotos = fotos;

        }
    }

    public void share(List<File> files) {

        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder
                .from(this)
                .setType("image/*")
                .setChooserTitle("Share Photo");

        for (File file :
                files) {
            Uri uri = FileProvider.getUriForFile(this, "br.com.ggsoftware.protegephotospro.fileprovider", file);
            builder.addStream(uri);
        }

        builder.startChooser();
        cancelarSelecao();

    }

    private class SalvarImagem extends AsyncTask<Uri, Void, Boolean> {
        protected Boolean doInBackground(Uri... uri) {

            Uri selectedImage = uri[0];

            try {
                String filename = queryName(getContentResolver(), selectedImage);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(GaleriaActivity.this.getContentResolver(), selectedImage);
                ImageSaver imageSaver = new ImageSaver(GaleriaActivity.this);
                ImageDAO imageDAO = new ImageDAO(GaleriaActivity.this);
                imageSaver.save(bitmap, filename);
                imageDAO.insereDado(filename, pastaSelecionada.getNomePasta());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.btn_novas_fotos)).setVisibility(View.GONE);
                listaImagens = imagemDAO.listarPorPasta(pastaSelecionada.getNomePasta());
                mFotos = Foto.getSpacePhotos(listaImagens);

                ImageGalleryAdapter adapter = new ImageGalleryAdapter(GaleriaActivity.this, Foto.getSpacePhotos(listaImagens));
                recyclerView.setAdapter(adapter);
                progressBarCircular.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "Falha ao salvar imagem", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class SalvarImagens extends AsyncTask<ClipData, Void, Boolean> {
        protected Boolean doInBackground(ClipData... clipdatas) {

            ClipData clipData = clipdatas[0];

            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();

                try {
                    String filename = queryName(getContentResolver(), uri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(GaleriaActivity.this.getContentResolver(), uri);


                    ImageSaver imageSaver = new ImageSaver(GaleriaActivity.this);
                    ImageDAO imageDAO = new ImageDAO(GaleriaActivity.this);
                    imageSaver.save(bitmap, filename);
                    imageDAO.insereDado(filename, pastaSelecionada.getNomePasta());
                    progress++;
                    progressBar.setProgress(progress, true);
                    textViewDialog.setText(progress + "/" + quantidadeArquivos);
                } catch (IOException e) {
                    e.printStackTrace();

                    return false;
                }


            }
            return true;
        }


        protected void onPostExecute(Boolean result) {

            ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_novas_fotos)).setVisibility(View.GONE);

            listaImagens = imagemDAO.listarPorPasta(pastaSelecionada.getNomePasta());

            views = new ArrayList<>();
            ImageGalleryAdapter adapter = new ImageGalleryAdapter(GaleriaActivity.this, Foto.getSpacePhotos(listaImagens));


            mFotos = Foto.getSpacePhotos(listaImagens);
            recyclerView.setAdapter(adapter);
            dialog.dismiss();


        }


    }

    private class SalvarImagens2 extends AsyncTask<ClipData, Void, ClipData[]> {
        protected ClipData[] doInBackground(ClipData... clipdatas) {

            ClipData clipData = clipdatas[0];


            ClipData.Item item = clipData.getItemAt(progress);
            Uri uri = item.getUri();

            try {
                String filename = queryName(getContentResolver(), uri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(GaleriaActivity.this.getContentResolver(), uri);


                ImageSaver imageSaver = new ImageSaver(GaleriaActivity.this);
                ImageDAO imageDAO = new ImageDAO(GaleriaActivity.this);
                imageSaver.save(bitmap, filename);
                imageDAO.insereDado(filename, pastaSelecionada.getNomePasta());

            } catch (IOException e) {
                e.printStackTrace();

                return clipdatas;
            }



            return clipdatas;
        }


        protected void onPostExecute(ClipData[] clipData) {

            progress++;
            progressBar.setProgress(progress, true);
            textViewDialog.setText(progress + "/" + quantidadeArquivos);
            if(progress <  quantidadeArquivos){

                new SalvarImagens2().execute(clipData);

            }else {

                ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.btn_novas_fotos)).setVisibility(View.GONE);

                listaImagens = imagemDAO.listarPorPasta(pastaSelecionada.getNomePasta());

                views = new ArrayList<>();
                ImageGalleryAdapter adapter = new ImageGalleryAdapter(GaleriaActivity.this, Foto.getSpacePhotos(listaImagens));


                mFotos = Foto.getSpacePhotos(listaImagens);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }

        }


    }
    public void abrirDialogInfoInvisivel(View v){
        final androidx.appcompat.app.AlertDialog dialog;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(GaleriaActivity.this)
                .setTitle(getString(R.string.title_criar_nova_pasta_informacoes))
                .setMessage(getString(R.string.txt_pasta_invisivel))
                .setPositiveButton(R.string.btn_entendi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })

                .setCancelable(true);

        dialog = builder.create();

        dialog.show();
    }
    private void showProgressDialog(final int quantidadeArquivosAnexados){

        quantidadeArquivos = quantidadeArquivosAnexados;
        final View alertDialogView = LayoutInflater.from(GaleriaActivity.this).inflate
                (R.layout.dialog_progress, null);
        textViewDialog = alertDialogView.findViewById(R.id.txtProgress);
        textViewDialog.setText("0/" + quantidadeArquivosAnexados);
        progressBar = alertDialogView.findViewById(R.id.progressBar1);
        progressBar.setMax(quantidadeArquivosAnexados);



        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(GaleriaActivity.this)
                .setTitle(getString(R.string.msg_title_proteger_fotos))
                .setView(alertDialogView)
                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salvadorImagems.cancel(true);
                        progress = quantidadeArquivos;
                        salvadorImagems.onPostExecute(null);
                        dialog.dismiss();
                    }
                });



        dialog = builder.create();

        dialog.show();


    }
}
