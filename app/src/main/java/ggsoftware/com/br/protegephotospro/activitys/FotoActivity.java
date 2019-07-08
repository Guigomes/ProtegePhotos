package ggsoftware.com.br.protegephotospro.activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import ggsoftware.com.br.protegephotospro.BuildConfig;
import ggsoftware.com.br.protegephotospro.utils.ImageSaver;
import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.entidades.Foto;

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

    public void share(View v){
        Toast.makeText(getApplicationContext(), "share", Toast.LENGTH_SHORT).show();
        share2();
    }
    public void share(){
        // create new Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);

// set flag to give temporary permission to external app to use your FileProvider
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

// generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
        Uri uri = FileProvider.getUriForFile(this, "br.com.ggsoftware.protegephotospro.fileprovider", file);

// I am opening a PDF file so I give it a valid MIME type
        intent.setDataAndType(uri, "image/png");

// validate that the device can open your File!
        PackageManager pm = getPackageManager();
        if (intent.resolveActivity(pm) != null) {
            startActivity(intent);
        }
    }

public void share2(){


    Uri uri = FileProvider.getUriForFile(this, "br.com.ggsoftware.protegephotospro.fileprovider", file);
    ShareCompat.IntentBuilder
            .from(this)
            .setType("image/*")
            .setChooserTitle("Share Photo")
            .addStream(uri)
            .startChooser();
}
    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public void nextFoto(View v){
        List<Foto> fotos = Foto.getFotos();
        for(int i = 0;i< fotos.size(); i++){
    if(fotos.get(i).getId() == foto.getId()){
        if(i < fotos.size() -1){
            foto = fotos.get(i+1);
        }else{
            foto = fotos.get(0);
        }
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
        Log.i("TESTE","TESTE");

    }
}
