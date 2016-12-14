package cn.alexwan.ccamera;

import android.app.Fragment;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import cn.alexwan.ccamera.widget.CCameraPreview;

/**
 * Created by alexwan on 2016/12/13.
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback , Camera.PictureCallback {

    public static final String TAG = CameraFragment.class.getSimpleName();
    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_FLASH_KEY = "flash_mode";
    public static final String IMAGE_INFO = "image_info";

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private int mCameraID;
    private String mFlashMode;
    private Camera mCamera;
    private CCameraPreview mPreviewView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mIsSafeToTakePhoto = false;



    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    //
}
