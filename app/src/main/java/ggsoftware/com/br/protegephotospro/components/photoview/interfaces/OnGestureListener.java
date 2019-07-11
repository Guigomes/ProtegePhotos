package ggsoftware.com.br.protegephotospro.components.photoview.interfaces;


public interface OnGestureListener {

    void onDrag(float dx, float dy);


    void onFling(float startX, float startY, float velocityX,
                 float velocityY);

    void onScale(float scaleFactor, float focusX, float focusY);

}