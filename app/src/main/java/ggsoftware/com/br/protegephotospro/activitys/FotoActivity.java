package ggsoftware.com.br.protegephotospro.activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import ggsoftware.com.br.protegephotospro.utils.ImageSaver;
import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.entidades.Foto;

public class FotoActivity extends AppCompatActivity {

    public static final String EXTRA_SPACE_PHOTO = "FotoActivity.SPACE_PHOTO";
    private PhotoView mImageView;

    Foto foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_photo);

        mImageView = (PhotoView) findViewById(R.id.image);
         foto = getIntent().getParcelableExtra(EXTRA_SPACE_PHOTO);

        ImageSaver imageSaver = new ImageSaver(FotoActivity.this);
        File file = imageSaver.loadFile(foto.getTitle());


        Glide.with(this)
                .load(file.getAbsolutePath())
                .asBitmap()
                .error(android.R.drawable.ic_delete)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);
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
