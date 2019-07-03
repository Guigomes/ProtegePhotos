package ggsoftware.com.br.protegephotospro.activitys;


import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ggsoftware.com.br.protegephotospro.Constantes;
import ggsoftware.com.br.protegephotospro.utils.ImageSaver;
import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.entidades.Foto;
import ggsoftware.com.br.protegephotospro.dao.ImageDAO;
import ggsoftware.com.br.protegephotospro.dao.ImagemVO;
import ggsoftware.com.br.protegephotospro.dao.PastaDAO;
import ggsoftware.com.br.protegephotospro.dao.PastaVO;
import ggsoftware.com.br.protegephotospro.components.pattern.ConfirmPatternActivity;

import static ggsoftware.com.br.protegephotospro.Constantes.*;

public class GaleriaActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private static final int PICK_IMAGE = 1;
    PastaVO pastaSelecionada;
    List<ImagemVO> listaImagens;
    ImageDAO imagemDAO;
    private ProgressBar spinner;

    private List<Foto> mFotos;
    private Context mContext;

    ArrayList<ImageGalleryAdapter.MyViewHolder> views = new ArrayList<>();
    boolean modoSelecao = false;
    ImageGalleryAdapter adapter;

    PastaDAO pastaDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        pastaDAO = new PastaDAO(GaleriaActivity.this);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        Bundle extras = getIntent().getExtras();

        List<Foto> mFotos = new ArrayList<>();


        String nomePasta = null;
        if (extras != null) {

            nomePasta = (String) extras.get("nomePasta");
        }
        if (nomePasta != null) {
            pastaSelecionada = pastaDAO.buscarPorNome(nomePasta);

        } else {

            Toast.makeText(getApplicationContext(), "Erro aco encontrar pasta", Toast.LENGTH_LONG).show();

        }

        setTitle(pastaSelecionada.getNomePasta());


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        imagemDAO = new ImageDAO(GaleriaActivity.this);


        listaImagens = imagemDAO.listarPorPasta(pastaSelecionada.getNomePasta());

        if (listaImagens.size() == 0) {
            ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.GONE);
        }
        adapter = new ImageGalleryAdapter(this, Foto.getSpacePhotos(listaImagens));
        recyclerView.setAdapter(adapter);

        registerForContextMenu(recyclerView);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.menu_galeria_modo_invisivel, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_alterar_nome_pasta:
                alterarNomePasta();
            case R.id.action_excluir_pasta:
                excluirPasta();
                break;
            case R.id.action_add_imagem:
                addImagem();
                break;
            case R.id.action_excluir_imagem:
                excluirImagem();
                break;
            case R.id.action_cancelar_selecao:
                cancelarSelecao();
                break;
            case R.id.action_new_folder:
                criarNovaPasta();
                break;
            case R.id.action_alterar_senha:
                alterarSenhaPasta();
                break;

            case R.id.action_remover_modo_invisivel:
                removerPastasModoInvisivel();
                break;
            case R.id.share:
                compartilharImagem();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void removerPastasModoInvisivel() {

        pastaSelecionada.setInvisivel(0);
        pastaDAO.updatePasta(pastaSelecionada);

        startActivity(new Intent(GaleriaActivity.this, MainActivity.class));
        Toast.makeText(GaleriaActivity.this, getString(R.string.msg_sucesso_remover_pasta_modo_invisivel), Toast.LENGTH_SHORT).show();
    }


    private void alterarNomePasta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GaleriaActivity.this);
        builder.setTitle(getString(R.string.txt_informe_nome_novo_pasta));


        final EditText input = new EditText(GaleriaActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setTextColor(Color.BLACK);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.btn_alterar_nome_pasta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String novoNomePasta = input.getText().toString();

                pastaSelecionada.setNomePasta(novoNomePasta);
                pastaDAO.updatePasta(pastaSelecionada);
                startActivity(new Intent(GaleriaActivity.this, GaleriaActivity.class));
                Toast.makeText(GaleriaActivity.this, getString(R.string.msg_sucesso_alterar_nome_pasta), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.btn_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

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


    }

    private void compartilharImagem() {
        int count = adapter.getItemCount();


        for (int i = 0; i < count; i++) {

            Foto foto = mFotos.get(i);
            if (foto.getSelected() == 1) {
                try {
                    Toast.makeText(this, foto.getFilepath(), Toast.LENGTH_SHORT).show();
                    File file = new ImageSaver(GaleriaActivity.this).loadFile(foto.getTitle());

                    File imagePath = new File(getFilesDir(), "app_images");
                    File newFile = new File(imagePath, foto.getTitle());

                    Uri imageUri = FileProvider.getUriForFile(GaleriaActivity.this,
                            getString(R.string.file_provider_authority),
                            newFile);


                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    shareIntent.setType("image/*");
                    // Launch sharing dialog for image
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }


        cancelarSelecao();


    }

    private void criarNovaPasta() {
        AlertDialog.Builder builderCriarNovaPasta = new AlertDialog.Builder(GaleriaActivity.this);
        builderCriarNovaPasta.setTitle(getString(R.string.txt_informe_nome_pasta));


        final EditText inputNomePasta = new EditText(GaleriaActivity.this);

        inputNomePasta.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNomePasta.setTextColor(Color.BLACK);
        builderCriarNovaPasta.setView(inputNomePasta);

        builderCriarNovaPasta.setPositiveButton(getString(R.string.btn_criar_pasta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomePasta = inputNomePasta.getText().toString();

                Intent it = new Intent(GaleriaActivity.this, EscolherPadraoActivity.class);

                it.putExtra("idPasta", -1);
                it.putExtra("nomePasta", nomePasta);
                startActivity(it);
            }
        });

        builderCriarNovaPasta.setNegativeButton(getString(R.string.btn_cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builderCriarNovaPasta.show();
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

    private void excluirPasta() {
        PastaDAO pastaDAO = new PastaDAO(GaleriaActivity.this);

        pastaDAO.excluir(pastaSelecionada.getId());

        Toast.makeText(GaleriaActivity.this, getString(R.string.msg_sucesso_deletar_pasta), Toast.LENGTH_SHORT).show();

        startActivity(new Intent(GaleriaActivity.this, MainActivity.class));


    }

    private void cancelarSelecao() {

        for (Foto foto : mFotos) {
            foto.setSelected(0);
        }

        int count = views.size();
        for (int i = 0; i < count; i++) {
            ImageGalleryAdapter.MyViewHolder v = views.get(i);

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

            menu.findItem(R.id.action_alterar_senha).setVisible(false);

            menu.findItem(R.id.action_new_folder).setVisible(false);

        } else {
            menu.findItem(R.id.action_excluir_imagem).setVisible(false);
            menu.findItem(R.id.action_cancelar_selecao).setVisible(false);
            menu.findItem(R.id.action_add_imagem).setVisible(true);
            menu.findItem(R.id.action_alterar_senha).setVisible(true);

            menu.findItem(R.id.action_new_folder).setVisible(true);

        }
        return true;
    }


    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }


    public void addImagem() {
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
                        spinner.setVisibility(View.VISIBLE);

                        new SalvarImagens().execute(clipData);
                    } else {
                        Uri selectedImage = imageReturnedIntent.getData();
                        spinner.setVisibility(View.VISIBLE);

                        new SalvarImagem().execute(selectedImage);
                    }
                }
                break;
            case Constantes.ALTERAR_SENHA:
                String nomePasta = (String) imageReturnedIntent.getExtras().get("nomePasta");
                String pattern = (String) imageReturnedIntent.getExtras().get("pattern");


                PastaDAO pastaDAO = new PastaDAO(GaleriaActivity.this);
                PastaVO pastaVO = pastaDAO.buscarPorNome(nomePasta);
                if (pastaVO != null) {
                    pastaVO.setSenhaPasta(pattern);

                    boolean sucesso = pastaDAO.updatePasta(pastaVO);


                    if (sucesso) {
                        Toast.makeText(GaleriaActivity.this, getString(R.string.msg_sucesso_alterar_senha), Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(GaleriaActivity.this, GaleriaActivity.class);
                        it.putExtra("nomePasta", nomePasta);
                        startActivity(it);
                        finish();

                    } else {
                        Toast.makeText(GaleriaActivity.this, getString(R.string.msg_erro_criar_pasta), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GaleriaActivity.this, getString(R.string.msg_pasta_nao_existe), Toast.LENGTH_SHORT).show();

                }
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
                listaImagens = imagemDAO.listarPorPasta(pastaSelecionada.getNomePasta());
                mFotos = Foto.getSpacePhotos(listaImagens);

                ImageGalleryAdapter adapter = new ImageGalleryAdapter(GaleriaActivity.this, Foto.getSpacePhotos(listaImagens));
                recyclerView.setAdapter(adapter);

            } else {
                Toast.makeText(getApplicationContext(), "Falha ao salvar imagem", Toast.LENGTH_SHORT).show();
            }
            spinner.setVisibility(View.GONE);

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

                } catch (IOException e) {
                    e.printStackTrace();

                    return false;
                }


            }
            return true;
        }


        protected void onPostExecute(Boolean result) {

            ((TextView) findViewById(R.id.txt_pasta_vazia)).setVisibility(View.GONE);
            listaImagens = imagemDAO.listarPorPasta(pastaSelecionada.getNomePasta());

            views = new ArrayList<>();
            ImageGalleryAdapter adapter = new ImageGalleryAdapter(GaleriaActivity.this, Foto.getSpacePhotos(listaImagens));


            mFotos = Foto.getSpacePhotos(listaImagens);
            recyclerView.setAdapter(adapter);

            spinner.setVisibility(View.GONE);


        }


    }
}
