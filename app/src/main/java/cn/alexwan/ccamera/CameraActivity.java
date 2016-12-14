package cn.alexwan.ccamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import static android.app.Activity.RESULT_OK;

/**
 * CameraActivity
 * Created by alexwan on 2016/12/13.
 */
public class CameraActivity extends FragmentActivity{

    private static final String TAG = CameraActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void returnPhotoUri(@NonNull Uri uri) {
        Intent data = new Intent();
        data.setData(uri);
        setResult(RESULT_OK, data);
        finish();
    }

}
