package cn.alexwan.ccamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * CameraActivity
 * Created by alexwan on 2016/12/13.
 */
public class CameraActivity extends AppCompatActivity{

    private static final String TAG = CameraActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ccamera_activity_camera);
        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container , CameraFragment.newInstance() , CameraFragment.TAG)
                    .commit();
        }
    }

    public void returnPhotoUri(@NonNull Uri uri) {
        Intent data = new Intent();
        data.setData(uri);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onCancel(View view){
        getSupportFragmentManager().popBackStack();
    }

}
