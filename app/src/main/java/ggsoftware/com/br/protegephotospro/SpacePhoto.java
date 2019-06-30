package ggsoftware.com.br.protegephotospro;

/**
 * Created by f3861879 on 02/10/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import ggsoftware.com.br.protegephotospro.dao.ImagemVO;

public class SpacePhoto implements Parcelable {

    private String mUrl;
    private String mTitle;

    private int selected;
    private int id;

    private String filepath;

    private static List<SpacePhoto> fotos;

    public static List<SpacePhoto>  getFotos(){

        return fotos;
    }

    public  int getId(){

        return id;
    }


    public SpacePhoto(String url, String title, int id) {
        mUrl = url;
        this.id = id;
        mTitle = title;
    }

    protected SpacePhoto(Parcel in) {
        mUrl = in.readString();
        mTitle = in.readString();
        id  = in.readInt();
        filepath = in.readString();
    }

    public static final Creator<SpacePhoto> CREATOR = new Creator<SpacePhoto>() {
        @Override
        public SpacePhoto createFromParcel(Parcel in) {
            return new SpacePhoto(in);
        }

        @Override
        public SpacePhoto[] newArray(int size) {
            return new SpacePhoto[size];
        }
    };

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public static  List<SpacePhoto> getSpacePhotos(  List<ImagemVO> imagens) {


        List<SpacePhoto> photos = new ArrayList<>();
        int i = 0;
        for(ImagemVO imagemVO : imagens){
            photos.add(new SpacePhoto(imagemVO.getDiretorio(), imagemVO.getNome(), imagemVO.getId()));

        }
        fotos = photos;
        return fotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUrl);
        parcel.writeString(mTitle);
        parcel.writeInt(id);

        parcel.writeInt(selected);


    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
