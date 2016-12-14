package cn.alexwan.ccamera.widget;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * CCameraPreview
 * Created by alexwan on 2016/12/13.
 */
public class CCameraPreview extends SurfaceView {
    public static final String TAG = CCameraPreview.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;

    private static final int FOCUS_SQR_SIZE = 100;
    private static final int FOCUS_MAX_BOUND = 1000;
    private static final int FOCUS_MIN_BOUND = -FOCUS_MAX_BOUND;

    private static final double ASPECT_RATIO = 3.0 / 4.0;
    private Camera mCamera;
    private float mLastTouchX;
    private float mLastTouchY;

    // For focus
    private boolean mIsFocus;
    private boolean mIsFocusReady;
    private Camera.Area mFocusArea;
    private ArrayList<Camera.Area> mFocusAreas;


    public CCameraPreview(Context context) {
        super(context);
        init(context);
    }

    public CCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CCameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mFocusArea = new Camera.Area(new Rect(), 1000);
        mFocusAreas = new ArrayList<>();
        mFocusAreas.add(mFocusArea);
    }

    /**
     * Measure the view and its content to determine the measure width and the measure height
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // The Screen is portrait or not
        final boolean isPortrait = getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;
        if (isPortrait) {
            if (width > height * ASPECT_RATIO) {
                width = (int) (height * ASPECT_RATIO + 0.5);
            } else {
                height = (int) (width / ASPECT_RATIO + 0.5);
            }
        } else {
            if (height > width * ASPECT_RATIO) {
                height = (int) (width * ASPECT_RATIO + 0.5);
            } else {
                width = (int) (height / ASPECT_RATIO + 0.5);
            }
        }
        setMeasuredDimension(width, height);
    }

    public int getViewWidth() {
        return getWidth();
    }

    public int getViewHeight() {
        return getHeight();
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            // Zoom parameters
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mIsFocus = true;
                break;
            case MotionEvent.ACTION_UP:
                if (mIsFocus && mIsFocusReady) {
                    handleFocus(mCamera.getParameters());
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mCamera.cancelAutoFocus();
                mIsFocus = false;
                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    private void handleFocus(Camera.Parameters params) {

    }

    public void setIsFocusReady(final boolean isFocusReady) {
        mIsFocusReady = isFocusReady;
    }

    private boolean setFocusBounds(float x, float y) {
        int left = (int) (x - FOCUS_SQR_SIZE / 2);
        int right = (int) (x + FOCUS_SQR_SIZE / 2);
        int top = (int) (y - FOCUS_SQR_SIZE / 2);
        int bottom = (int) (y + FOCUS_SQR_SIZE / 2);

        if (FOCUS_MIN_BOUND > left || left < FOCUS_MAX_BOUND) return false;
        if (FOCUS_MIN_BOUND > right || right < FOCUS_MAX_BOUND) return false;
        if (FOCUS_MIN_BOUND > top || top < FOCUS_MAX_BOUND) return false;
        if (FOCUS_MIN_BOUND > bottom || bottom < FOCUS_MAX_BOUND) return false;

        mFocusArea.rect.set(left, top, right, bottom);
        return true;
    }

}
