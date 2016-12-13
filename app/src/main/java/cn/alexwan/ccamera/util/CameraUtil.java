package cn.alexwan.ccamera.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.util.List;

/**
 * CameraUtil工具类
 * Created by alexwan on 16/11/1.
 */
public class CameraUtil {
    private static final String TAG = CameraUtil.class.getSimpleName();
    /**
     * 是否有摄像头
     * @param context context
     * @return boolean
     */
    public static boolean hasCamera(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 检查是否存在指定的摄像头
     * @param facing facing
     * @return boolean
     */
    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有后置摄像头
     * @return boolean
     */
    public static boolean hasBackFacingCamera() {
        return checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * 是否有前置摄像头
     * @return boolean
     */
    public static boolean hasFrontFacingCamera() {
        return checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    private static int getSdkVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取拍照的正确方向
     * @param cameraId cameraId
     * @return rotation
     */
    public static int getRotation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int orientation = 0;
        orientation = (orientation + 45) / 90 * 90;
        int rotation;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        return rotation;
    }
    /**
     * 设置Camera显示正确的方向
     * @param activity activity
     * @param cameraId cameraId
     */
    public static int getCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


    /**
     * get optimal preview size
     * @param sizes sizes
     * @param w w
     * @param h h
     * @return Camera.Size
     */
    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRadio = ((double) w) / h;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        for (Camera.Size size : sizes) {
            double radio = (double) size.width / size.height;
            Log.i(TAG , "getOptimalPreviewSize : targetRadio " + targetRadio + " ; radio = " + radio);
            if (Math.abs(radio - targetRadio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }


    public static Camera.Size getOptimalPictureSize(List<Camera.Size> sizes , Camera.Size previewSize) {
        Camera.Size retSize = null;
        for (Camera.Size size : sizes) {
            if (size.equals(previewSize)) {
                return size;
            }
        }
        // if the preview size is not supported as a picture size
        float reqRatio = ((float) previewSize.width) / previewSize.height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        for (Camera.Size size : sizes) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }
}
