package com.github.songnick;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by SongNick on 15/12/27.
 */
public class FloatWindow implements View.OnTouchListener{

    private Context mContext = null;

    private Point mWindowPosition = null;

    private PointF mTouchPosition = null;

    private WindowManager mWindowManager = null;
    private View mFloatView = null;
    private boolean mIsShowed = false;
    private WindowManager.LayoutParams mWmLayoutParams = null;

    public FloatWindow(Context context){
        mContext = context;
        init();
    }

    /**
     * initialize the {@link #mWindowManager} and
     * {@link #mFloatView} which is floating view
     * */
    private void init(){
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        mFloatView = LayoutInflater.from(mContext).inflate(R.layout.float_window_layout, null);
        mWindowPosition = new Point();
        mTouchPosition = new PointF();

    }

    /**
     * show the floating view,
     * just add view to {@link WindowManager}
     * */
    public void show(){
        if (!mIsShowed){
            WindowManager.LayoutParams wl = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            wl.gravity = Gravity.LEFT | Gravity.TOP;
            mWmLayoutParams = wl;
            mWindowManager.addView(mFloatView, wl);
            mFloatView.setOnTouchListener(this);
            mIsShowed = true;
        }
    }

    /**
     * if there is view already added to {@link WindowManager}
     * remove this view immediately
     * */
    public void hide(){
        if (mIsShowed){
            mWindowManager.removeViewImmediate(mFloatView);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                //set original params and action down aix
                mWindowPosition.set(mWmLayoutParams.x, mWmLayoutParams.y);
                mTouchPosition.set(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                int x = mWindowPosition.x + (int)(event.getRawX() - mTouchPosition.x);
                int y = mWindowPosition.y + (int)(event.getRawY() - mTouchPosition.y);
                updateWindow(x, y);
                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    /**
     * when the user slide this view, move it align user's pointer
     * @param x the position to move int X axi
     * @param y the position to move int Y axi
     * */
    private void updateWindow(int x, int y){
        mWmLayoutParams.x = x;
        mWmLayoutParams.y = y;
        mWindowManager.updateViewLayout(mFloatView, mWmLayoutParams);
    }
}
