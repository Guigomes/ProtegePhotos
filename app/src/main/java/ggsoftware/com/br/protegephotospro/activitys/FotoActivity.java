package ggsoftware.com.br.protegephotospro.activitys;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.List;


import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.components.photoview.interfaces.OnSwipeLeftListener;
import ggsoftware.com.br.protegephotospro.components.photoview.interfaces.OnSwipeRightListener;
import ggsoftware.com.br.protegephotospro.components.photoview.interfaces.OnToogleActionBar;
import ggsoftware.com.br.protegephotospro.entidades.Foto;
import ggsoftware.com.br.protegephotospro.utils.ImageSaver;
import ggsoftware.com.br.protegephotospro.components.photoview.PhotoView;

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
                toggleActionBar();
                proximaFoto();
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
                fotoAnterior();
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

    public void share(View v) {

        Uri uri = FileProvider.getUriForFile(this, "br.com.ggsoftware.protegephotospro.fileprovider", file);
        ShareCompat.IntentBuilder
                .from(this)
                .setType("image/*")
                .setChooserTitle("Share Photo")
                .addStream(uri)
                .startChooser();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


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
