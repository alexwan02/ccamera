package cn.alexwan.ccamera;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import cn.alexwan.ccamera.util.CameraHelper;
import cn.alexwan.ccamera.util.CameraUtil;
import cn.alexwan.ccamera.widget.CameraPreview;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private CameraOverCallback mCameraCallback;
    private FrameLayout mContainer;
    private CameraPreview mCameraPreview;
    private View mTakePictureBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera_layout);

        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
//        Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
//        startActivityForResult(startCustomCameraIntent, 1000);

        mContainer = (FrameLayout) findViewById(R.id.container);
        mTakePictureBtn = findViewById(R.id.take_picture);
        mTakePictureBtn.setOnClickListener(this);
        // default camera degree
        int backFacingDegree = CameraUtil.getCameraDisplayOrientation(this, CAMERA_FACING_BACK);
        //
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = getWindowManager();
        wm.getDefaultDisplay().getMetrics(dm);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dm.widthPixels , dm.widthPixels);
        mCameraPreview = new CameraPreview(this, backFacingDegree);
        mCameraPreview.setLayoutParams(params);
        mContainer.addView(mCameraPreview, 1);
        mCameraCallback = mCameraPreview;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraPreview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraHelper.getInstance().doStopPreview();
        CameraHelper.getInstance().doStopCamera();
        // mContainer.removeView(mCameraPreview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.capture_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_switch:
                CameraHelper.getInstance().doSwitchCameraFacing(mCameraCallback);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.take_picture:
                CameraHelper.getInstance().doTakePicture(0, 0, 0, 0);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}