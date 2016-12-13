package cn.alexwan.ccamera.util;

import android.util.Log;

import cn.alexwan.ccamera.BuildConfig;

/**
 * Created by alexwan on 2016/12/13.
 */
public class LogUtils {
    private static final String LOG_PREFIX = "ccamera_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void v(String tag, Object... messages) {
        log(tag, Log.VERBOSE, null, messages);
    }

    public static void d(String tag, Object... messages) {
        log(tag, Log.DEBUG, null, messages);
    }

    public static void d(String tag, String message) {
        log(tag, Log.DEBUG, null, message);
    }

    public static void i(String tag, String message) {
        log(tag, Log.INFO, null, message);
    }

    public static void i(String tag, Object... messages) {
        log(tag, Log.INFO, null, messages);
    }

    public static void w(String tag, Object... messages) {
        log(tag, Log.WARN, null, messages);
    }

    public static void w(String tag, Throwable t, Object... messages) {
        log(tag, Log.WARN, t, messages);
    }

    public static void e(String tag, Object... messages) {
        log(tag, Log.ERROR, null, messages);
    }

    public static void e(String tag, Throwable t, Object... messages) {
        log(tag, Log.ERROR, t, messages);
    }

    public static void e(String tag, String message) {
        log(tag, Log.ERROR, null, message);
    }

    public static void e(String tag, String message, Throwable t) {
        log(tag, Log.ERROR, t, message);
    }

    public static void log(String tag, int level, Throwable t, Object... messages) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        String message;
        if (t == null && messages != null && messages.length == 1) {
            // handle this common case without the extra cost of creating a stringbuffer:
            message = messages[0].toString();
        } else {
            StringBuilder sb = new StringBuilder();
            if (messages != null) for (Object m : messages) {
                sb.append(m);
            }
            if (t != null) {
                sb.append("\n").append(Log.getStackTraceString(t));
            }
            message = sb.toString();
        }
        switch (level) {
            case Log.DEBUG:
                Log.d(tag, message);
                break;
            case Log.INFO:
                Log.i(tag, message);
                break;
            case Log.ERROR:
                Log.e(tag, message);
                break;
            default:
                Log.w(tag , message);
                break;
        }
    }
}
