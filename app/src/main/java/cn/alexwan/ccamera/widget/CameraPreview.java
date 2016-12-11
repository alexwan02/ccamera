package cn.alexwan.ccamera.widget;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import cn.alexwan.ccamera.CameraOverCallback;
import cn.alexwan.ccamera.util.CameraHelper;
import cn.alexwan.ccamera.util.CameraUtil;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;

/**
 * CameraPreview
 * Created by alexwan on 16/11/1.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, CameraOverCallback {
    private static final String TAG = CameraPreview.class.getSimpleName();
    private Camera.Size mPreviewSize;
    // private List<Camera.Size> mSupportedPreviewSizes;
    private float mPreviewRate;
    private int mFlashMode;
    private Camera mCamera;
    private int mDegree;
    private SurfaceHolder mHolder;

    public CameraPreview(Context context, int degree) {
        super(context);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.mCamera = CameraHelper.getInstance().doGetCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
        this.mDegree = degree;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraHelper.getInstance().doOpenCamera(this, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            Log.e(TAG, "holder.getSurface() == null");
            return;
        }
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
        // focusModes
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported
            parameters.setFocusMode(FOCUS_MODE_AUTO);
        }
        parameters.setRotation(CameraUtil.getRotation(0));
        mCamera.setParameters(parameters);
        //

        mCamera.setDisplayOrientation(mDegree);
        CameraHelper.getInstance().doStartPreview(holder, mPreviewRate, mFlashMode);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraHelper.getInstance().doStopPreview();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        List<Camera.Size> mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = CameraUtil.getOptimalPreviewSize(mSupportedPreviewSizes,
                    Math.max(width, height), Math.min(width, height));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void cameraRateChange(boolean isDefaultRating) {

    }

    @Override
    public void cameraFlashModeChange(int flashMode) {

    }

    @Override
    public void cameraFacingChanged(boolean hasFrontCamera, int cameraId) {
        if(!hasFrontCamera) return;
        CameraHelper.getInstance().doStopPreview();
        CameraHelper.getInstance().doStopCamera();
        Camera camera;
        if (cameraId == CAMERA_FACING_FRONT) {
            camera = CameraHelper.getInstance().doGetCameraInstance(CAMERA_FACING_BACK);
        } else {
            camera = CameraHelper.getInstance().doGetCameraInstance(CAMERA_FACING_FRONT);
        }
        // get Camera parameters
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
        //
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported
            parameters.setFocusMode(FOCUS_MODE_AUTO);
        }
        parameters.setRotation(CameraUtil.getRotation(cameraId == CAMERA_FACING_FRONT ? CAMERA_FACING_BACK : CAMERA_FACING_FRONT));
        camera.setParameters(parameters);
        // set correct display orientation
        camera.setDisplayOrientation(mDegree);
        //
        // set current camera
        this.mCamera = camera;
        requestLayout();
        CameraHelper.getInstance().doStartPreview(mHolder, 0, 0);
    }

    @Override
    public void cameraPhotoTaken(String path) {
        // TODO
        Log.i(TAG , "cameraPhotoTaken : path = " + path);
        CameraHelper.getInstance().doStartPreview(mHolder, 0, 0);
    }

    @Override
    public void cameraErrorReport(String errMsg) {
        Log.e(TAG , "cameraErrorReport : error = " + errMsg);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.i(TAG , "onPreviewFrame : " + data.length);

        // TODO: 16/11/8  
    }

    public void onResume(){
        this.mCamera = CameraHelper.getInstance().doGetCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
        if(this.mCamera == null){
            Log.e("Camera" , "this.mCamera == null");
        }
        CameraHelper.getInstance().doStartPreview(mHolder , 0 , 0);
    }
}
