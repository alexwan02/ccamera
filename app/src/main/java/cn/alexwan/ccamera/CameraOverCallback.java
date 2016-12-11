package cn.alexwan.ccamera;

import android.hardware.Camera;

/**
 * CameraOverCallback
 * 相机回调接口用于更新贴纸相机活动的UI
 * Created by alexwan on 16/10/31.
 */
public interface CameraOverCallback {
    public void cameraRateChange(boolean isDefaultRating);

    public void cameraFlashModeChange(int flashMode);

    public void cameraFacingChanged(boolean hasFrontCamera , int cameraId);

    public void cameraPhotoTaken(String path);

    public void cameraErrorReport(String errMsg);

    public void onPreviewFrame(byte[] data, Camera camera);
}
