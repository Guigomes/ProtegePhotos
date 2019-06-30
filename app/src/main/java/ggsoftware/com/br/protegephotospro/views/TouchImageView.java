package ggsoftware.com.br.protegephotospro.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by f3861879 on 20/11/2017.
 */

@SuppressLint("AppCompatCustomView")
public class TouchImageView extends ImageView {

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public TouchImageView(Context mContext){
        super(mContext);

        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.restore();
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }
}