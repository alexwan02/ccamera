package cn.alexwan.ccamera.util;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.alexwan.ccamera.CameraOverCallback;

import static cn.alexwan.ccamera.util.StorageHelper.MEDIA_TYPE_IMAGE;

/**
 * CameraOperationHelper
 * Created by alexwan on 16/10/31.
 */
public class CameraHelper {

    private static final String TAG = CameraHelper.class.getSimpleName();
    private static CameraHelper mCameraHelper;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean isPreviewInitialized = false;
    private boolean mSafeToTakePhoto = false;
    private CameraOverCallback mCameraCallback;
    private int mCameraId;
    private CPictureCallback mPictureCallback = new CPictureCallback();
    private CPreviewCallback mPreviewCallback = new CPreviewCallback();

    public static synchronized CameraHelper getInstance() {
        if (mCameraHelper == null) {
            mCameraHelper = new CameraHelper();
        }
        return mCameraHelper;
    }

    public Camera doGetCameraInstance(int cameraId) {
        return getCameraInstance(cameraId);
    }

    public void doOpenCamera(@NonNull CameraOverCallback callback, @NonNull SurfaceHolder holder) {
        try {
            mCameraCallback = callback;
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            mCameraCallback.cameraErrorReport("doOpenCamera error = " + e.getMessage());
        }
    }

    private Camera getCameraInstance(int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
            mCameraId = cameraId;
        } catch (Exception e) {
            Log.w(TAG, "doGetCameraInstance : error = " + e.getMessage());
        }
        return mCamera;
    }

    /**
     * do camera preview operation
     *
     * @param holder      holder
     * @param previewRate previewRate
     * @param flashMode   flashMode
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate, int flashMode) {
        // stop preview before making changes
        doStopPreview();
        // start open preview
        doOpenCamera(mCameraCallback, holder);
    }

    /**
     * stop camera preview
     */
    public void doStopPreview() {
        try {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.e(TAG, "doStopPreview : error = " + e.getMessage());
        }
    }

    /**
     *  release camera resource
     */
    public void doStopCamera() {
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * When this function returns, mCamera will be null.
     */
    public void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * take picture
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     */
    public void doTakePicture(int x, int y, int width, int height) {
        // mOverCallback.cameraPhotoTaken();
        if(!mSafeToTakePhoto) return;
        mCamera.takePicture(null, null , mPictureCallback);
    }

    public void doCameraErrorReport(String errorMsg) {
        Log.e(TAG , "doCameraErrorReport : error = " + errorMsg);
    }

    public void doSwitchCameraRate(CameraOverCallback callback) {
        boolean isDefaultRate = false;
        callback.cameraRateChange(isDefaultRate);
    }

    /**
     * switch camera facing
     * @param callback callback
     */
    public void doSwitchCameraFacing(CameraOverCallback callback) {
        boolean hasFrontCamera = CameraUtil.hasFrontFacingCamera();
        callback.cameraFacingChanged(hasFrontCamera , mCameraId);
    }

    public void doAutoFocus() {

    }

    public void doAutoFocusBeforeTakePhoto() {

    }

    /**
     * picture take call back
     */
    private class CPictureCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = StorageHelper.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if(pictureFile == null){
                Log.e(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                mCameraCallback.cameraPhotoTaken(pictureFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                Log.w(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.w(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    }

    private class CPreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            isPreviewInitialized = true;
            mSafeToTakePhoto = true;
            if(mCameraCallback != null){
                mCameraCallback.onPreviewFrame(data , camera);
            }
        }
    }
}
