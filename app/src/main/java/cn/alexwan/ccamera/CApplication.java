package cn.alexwan.ccamera;

import android.app.Application;

/**
 * Created by alexwan on 2016/12/12.
 */
public class CApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UnCeHandler catchException = UnCeHandler.getInstance();
        Thread.setDefaultUncaughtExceptionHandler(catchException);
    }
}
