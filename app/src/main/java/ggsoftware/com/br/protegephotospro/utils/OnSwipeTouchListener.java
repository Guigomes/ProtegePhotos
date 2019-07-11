package ggsoftware.com.br.protegephotospro.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
private Context ctx;
    public OnSwipeTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());

        this.ctx = ctx;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
/*
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick();
            return true;
        }
/*
        @Override
        public void onLongPress(MotionEvent e) {
            OnSwipeTouchListener.this.onLongPress();
        }
*/
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
        Toast.makeText(this.ctx,"SwipeRight", Toast.LENGTH_SHORT).show();
    }


    public void onSwipeLeft() {
        Toast.makeText(this.ctx,"SwipeLeft", Toast.LENGTH_SHORT).show();
    }

    public void onSwipeTop() {
        Toast.makeText(this.ctx,"SwipeTop", Toast.LENGTH_SHORT).show();
    }

    public void onSwipeBottom() {
        Toast.makeText(this.ctx,"SwipeBottom", Toast.LENGTH_SHORT).show();
    }
/*
    public void onClick() {
        Toast.makeText(this.ctx,"Click", Toast.LENGTH_SHORT).show();
    }

    public void onLongPress() {
        Toast.makeText(this.ctx,"LongPress", Toast.LENGTH_SHORT).show();
    }*/
}
