package com.github.songnick.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import com.github.songnick.utils.CameraUtils;

import java.util.List;

/**
 * Created by kascend on 2015/6/18.
 */
public class ChuShouSurfaceView extends SurfaceView {

    private Camera.Size mPreviewSize = null;
    private List<Camera.Size> mSupportedPreviewSizes = null;

    public ChuShouSurfaceView(Context context) {
        super(context);
    }

    public ChuShouSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChuShouSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSupportPreviewSizes(List<Camera.Size> supportPreviewSizes){
        mSupportedPreviewSizes = supportPreviewSizes;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        Log.d("", " surfaceCreated holder= my surface view width == " + width + " height = " + height);
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = CameraUtils.getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    public Camera.Size getPreviewSize(){
        return mPreviewSize;
    }
}
