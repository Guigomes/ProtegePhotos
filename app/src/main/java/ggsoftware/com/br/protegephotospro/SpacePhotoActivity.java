package ggsoftware.com.br.protegephotospro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class SpacePhotoActivity extends AppCompatActivity {

    public static final String EXTRA_SPACE_PHOTO = "SpacePhotoActivity.SPACE_PHOTO";
    private PhotoView mImageView;

    SpacePhoto spacePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_photo);

        mImageView = (PhotoView) findViewById(R.id.image);
         spacePhoto = getIntent().getParcelableExtra(EXTRA_SPACE_PHOTO);

        ImageSaver imageSaver = new ImageSaver(SpacePhotoActivity.this);
        File file = imageSaver.loadFile(spacePhoto.getTitle());


        Glide.with(this)
                .load(file.getAbsolutePath())
                .asBitmap()
                .error(android.R.drawable.ic_delete)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);
    }

    public void nextFoto(View v){
        List<SpacePhoto> fotos = SpacePhoto.getFotos();
        for(int i = 0;i< fotos.size(); i++){
    if(fotos.get(i).getId() == spacePhoto.getId()){
        if(i < fotos.size() -1){
            spacePhoto = fotos.get(i+1);
        }else{
            spacePhoto = fotos.get(0);
        }
        ImageSaver imageSaver = new ImageSaver(SpacePhotoActivity.this);
        File file = imageSaver.loadFile(spacePhoto.getTitle());


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
