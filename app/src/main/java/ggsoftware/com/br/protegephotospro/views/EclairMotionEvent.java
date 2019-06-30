package ggsoftware.com.br.protegephotospro.views;

/**
 * Created by f3861879 on 20/11/2017.
 */
import android.view.MotionEvent;

public class EclairMotionEvent extends WrapMotionEvent {

    protected EclairMotionEvent(MotionEvent event) {
        super(event);
    }

    public float getX(int pointerIndex) {
        return event.getX(pointerIndex);
    }

    public float getY(int pointerIndex) {
        return event.getY(pointerIndex);
    }

    public int getPointerCount() {
        return event.getPointerCount();
    }

    public int getPointerId(int pointerIndex) {
        return event.getPointerId(pointerIndex);
    }
}