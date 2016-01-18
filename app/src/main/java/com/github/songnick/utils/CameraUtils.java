/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.songnick.utils;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.github.songnick.AndroidServiceApp;
import com.github.songnick.view.ChuShouSurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Camera-related utility functions.
 */
public class CameraUtils {

	private static final String TAG = CameraUtils.class.getSimpleName();

	private Camera mCamera = null;
	private int mCurrentFace = Camera.CameraInfo.CAMERA_FACING_FRONT;

	private SurfaceHolder mSurfaceHolder = null;
	private ChuShouSurfaceView mSurfaceView = null;

	/**
	 * this interface is to notify the SurfaceView's holder
	 * camera is created
	 * */
    public interface CameraUtilsListener{
	    void cameraCreatedFinished(Camera camera);
    }

	private CameraUtilsListener mCameraUtilsListener = null;

	public static CameraUtils newInstance(){

		return  new CameraUtils();
	}

	/***
	 * set listener for this utils,when the camera is created or released
	 * @param listener  refer to {@link com.github.songnick.utils.CameraUtils.CameraUtilsListener}
	 * */
	public void setCameraUtilsListener(CameraUtilsListener listener){
		mCameraUtilsListener = listener;
	}

	/***
	 * acordding to {@link android.hardware.Camera.Size} and view's size to
	 * calculate the camera's screen size
	 *
	 * */
    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public Camera getCameraInstance(int face){
        Camera camera = null;
        try {
            int cameraNumber = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < cameraNumber; i++){
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == face){
                    camera = Camera.open(i);
                    break;
                }
            }
        }catch (Exception e){
			Log.d("", " open camera exception == " + e.getMessage());
		}
		if (camera != null){
			//switch camera success
			mCurrentFace = face;
			if (mCameraUtilsListener != null){
				mCameraUtilsListener.cameraCreatedFinished(camera);
			}
		}

        return camera;
    }

	/**
	 * confirm the default camera can work
	 * @see #cameraCanWork(int)
	 * */
	public boolean cameraCanWork(){

		return cameraCanWork(mCurrentFace);
	}

	/***
	 * according to camera's facing to get camera
	 * @param face facing of camera {@link android.hardware.Camera.CameraInfo#CAMERA_FACING_FRONT}
	 *             {@link android.hardware.Camera.CameraInfo#CAMERA_FACING_BACK}
	 *
	 * */
	public boolean cameraCanWork(int face){
		mCamera = getCameraInstance(face);
		return mCamera == null ? false : true;
	}

	/**
	 * start camera's preview
	 * and fix the orientation
	 * */
	public void startPreview(){
		if (mCamera != null){
			mCamera.startPreview();
			setPreviewOrientation();
		}
	}

	/**
	 * setup camera resource, and camera's parameters
	 * @param holder preview display holder {@link Camera#setPreviewDisplay(SurfaceHolder)}
	 * @param surfaceView which is to draw camera info
	 * */
	public boolean setupCamera(SurfaceHolder holder, ChuShouSurfaceView surfaceView){
		if (holder == null){
			throw new IllegalStateException("setup camera is illegal, the holder should not be null");
		}
		if (mCamera != null){
			try {
				mCamera.setPreviewDisplay(holder);
			}catch (IOException e){

			}
			mSurfaceHolder = holder;
			mSurfaceView = surfaceView;
			try {
				Camera.Parameters parameters = mCamera.getParameters();
				if (surfaceView != null){
					Camera.Size size = surfaceView.getPreviewSize();
					parameters.setPreviewSize(size.width, size.height);
				}
				List<String> focusModes = parameters.getSupportedFocusModes();
				List<String> whiteBalances = parameters.getSupportedWhiteBalance();
				if (whiteBalances != null && whiteBalances.size() > 0){
					if (whiteBalances.contains(Camera.Parameters.WHITE_BALANCE_AUTO)) {
						parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
					}
				}
				if (focusModes != null && focusModes.size() > 0){
					if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
						parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
					}
				}
				parameters.setRecordingHint(true);
				mCamera.setParameters(parameters);
			} catch (Exception e) {
				throw e;
			}
		}

		return mCamera == null? false : true;
	}

	/**
	 * Stops capturing and drawing preview frames to the surface,
	 * and release resource of camera
	 *
	 * */
	public void stopPreview(){
		if (mCamera != null){
			mCamera.stopPreview();
		}
	}

	/**
	 * release current camera
	 * the first step is to stop preview
	 * then release camera's resource
	 * */
	public void releaseCamera(){
		if (mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	/***
	 * switch facing of camera, facing orientation can
	 * refer {@link android.hardware.Camera.CameraInfo#CAMERA_FACING_BACK} and
	 * {@link android.hardware.Camera.CameraInfo#CAMERA_FACING_FRONT}
	 * */
	public boolean switchCameraFace(){
		releaseCamera();
		boolean switchSuccess = true;
		if (mCurrentFace == Camera.CameraInfo.CAMERA_FACING_FRONT){
			mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
		}else if (mCurrentFace == Camera.CameraInfo.CAMERA_FACING_BACK){
			mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}
		//if camera is null, open the previous camera
		if (mCamera == null){
			switchSuccess = false;
			mCamera = getCameraInstance(mCurrentFace);
		}
		setupCamera(mSurfaceHolder, mSurfaceView);
		startPreview();
		return switchSuccess;
	}

	/**
	 * according to current rotation
	 * set camera's display orientation
	 * invoke this method before start camera preview
	 * */
	private void setPreviewOrientation(){
		Camera.CameraInfo info = new Camera.CameraInfo();
		// Try to find a front-facing camera (e.g. for videoconferencing).
		int numCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numCameras; i++) {
			Camera.getCameraInfo(i, info);
			if (info.facing == mCurrentFace) {
				break;
			}
		}
		int defaultRotation = ((WindowManager) AndroidServiceApp.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (defaultRotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);
	}

}
