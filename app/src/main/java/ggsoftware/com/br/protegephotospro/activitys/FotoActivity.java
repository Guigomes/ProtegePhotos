package ggsoftware.com.br.protegephotospro.activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.components.photoview.interfaces.OnSwipeLeftListener;
import ggsoftware.com.br.protegephotospro.components.photoview.interfaces.OnSwipeRightListener;
import ggsoftware.com.br.protegephotospro.components.photoview.interfaces.OnToogleActionBar;
import ggsoftware.com.br.protegephotospro.dao.ImageDAO;
import ggsoftware.com.br.protegephotospro.entidades.Foto;
import ggsoftware.com.br.protegephotospro.utils.ImageSaver;
import ggsoftware.com.br.protegephotospro.components.photoview.PhotoView;
import ggsoftware.com.br.protegephotospro.utils.Utils;

public class FotoActivity extends AppCompatActivity {

    public static final String EXTRA_SPACE_PHOTO = "FotoActivity.SPACE_PHOTO";
    private PhotoView mImageView;

    Foto foto;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_photo);

        mImageView = (PhotoView) findViewById(R.id.image);

        mImageView.setOnSwipeRight(new OnSwipeRightListener() {
            @Override
            public void onSwipeRight() {
                fotoAnterior();

            }
        });

        mImageView.setOnToogleActionBar(new OnToogleActionBar() {
            @Override
            public void toogleActionBar() {
                toggleActionBar();

            }
        });

        mImageView.setOnSwipeLeft(new OnSwipeLeftListener() {

            @Override
            public void onSwipeLeft() {
                proximaFoto();
            }
        });
        foto = getIntent().getParcelableExtra(EXTRA_SPACE_PHOTO);

        ImageSaver imageSaver = new ImageSaver(FotoActivity.this);
        file = imageSaver.loadFile(foto.getTitle());


        Glide.with(this)
                .load(file.getAbsolutePath())
                .asBitmap()
                .error(android.R.drawable.ic_delete)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);

    }


    private void toggleActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    public void fotoAnterior() {
        List<Foto> fotos = Foto.getFotos();
        for (int i = 0; i < fotos.size(); i++) {
            if (fotos.get(i).getId() == foto.getId()) {
                if (i > 0) {
                    foto = fotos.get(i - 1);

                    ImageSaver imageSaver = new ImageSaver(FotoActivity.this);
                    File file = imageSaver.loadFile(foto.getTitle());


                    Glide.with(this)
                            .load(file.getAbsolutePath())
                            .asBitmap()
                            .error(android.R.drawable.ic_delete)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(mImageView);

                    break;
                }
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.action_excluir_imagem:
                abrirDialogExcluirFotos();
                break;


            case R.id.action_share:
                share();
                break;

            case R.id.action_download_imagem:

                    int permissionCheck = ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                    abrirDialogExportarFotos();


                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void share() {
        File file = new ImageSaver(FotoActivity.this).loadFile(foto.getTitle());

        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder
                .from(this)
                .setType("image/*")
                .setChooserTitle("Share Photo");

        Uri uri = FileProvider.getUriForFile(this, "br.com.ggsoftware.protegephotospro.fileprovider", file);
        builder.addStream(uri);


        builder.startChooser();

    }


    private void abrirDialogExcluirFotos(){
        android.app.AlertDialog.Builder dialogExcluir =  new android.app.AlertDialog.Builder(new ContextThemeWrapper(FotoActivity.this, R.style.Dialog));

        String  mensagemExcluirFotos = getString(R.string.msg_excluir_foto, foto.getTitle() );

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
        android.app.AlertDialog.Builder dialogExcluir =  new android.app.AlertDialog.Builder(new ContextThemeWrapper(FotoActivity.this, R.style.Dialog));

        String  mensagemExportarFotos = getString(R.string.msg_exportar_foto, foto.getTitle());

        dialogExcluir.setTitle(R.string.msg_title_exportar_foto)
                .setMessage(mensagemExportarFotos)
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

    private void excluirImagem() {

        new ImageDAO(FotoActivity.this).excluir(foto.getId());


        Utils.notificar(mImageView, getString(R.string.msg_sucesso_deletar_foto));

        finish();

    }
    private void downloadImage() throws FileNotFoundException {

        File file = new ImageSaver(FotoActivity.this).loadFile(foto.getTitle());
        boolean salvo = Utils.writeFileExternalStorage(FotoActivity.this, file);

        if(salvo) {
            Utils.notificar(mImageView,getString(R.string.msg_download_sucesso_2, getString(R.string.app_name)));


        }else{
            Utils.notificar(mImageView, getString(R.string.msg_download_erro));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_foto, menu);
        return true;
    }
    public void proximaFoto() {
        List<Foto> fotos = Foto.getFotos();
        for (int i = 0; i < fotos.size(); i++) {
            if (fotos.get(i).getId() == foto.getId()) {
                if (i < fotos.size() - 1) {
                    foto = fotos.get(i + 1);

                    ImageSaver imageSaver = new ImageSaver(FotoActivity.this);
                    File file = imageSaver.loadFile(foto.getTitle());


                    Glide.with(this)
                            .load(file.getAbsolutePath())
                            .asBitmap()
                            .error(android.R.drawable.ic_delete)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(mImageView);

                    break;
                }
            }
        }

    }
}
