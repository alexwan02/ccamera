package cn.alexwan.ccamera;


import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

/**
 * 程序异常捕捉类
 * Created by abel on 14/12/20.
 */
public class UnCeHandler implements Thread.UncaughtExceptionHandler {
    private static final String ERROR_LOG_DIR = "log";
    private static final UnCeHandler unceHandler = new UnCeHandler();
    private static final Thread.UncaughtExceptionHandler mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    public static final String TAG = UnCeHandler.class.getSimpleName();

    private UnCeHandler() {
    }

    /**
     * 做成单例
     *
     * @return UnCeHandler
     */
    public static UnCeHandler getInstance() {
        return unceHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "Error : ", ex);
        //todo异常信息发往服务器-pzz
        if (!handleException(thread, ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    private boolean handleException(final Thread thread, Throwable ex) {
        if (ex == null) {
            return true;
        }

        final String logMessage = String.format("%s\r\n\r\nThread: %d\r\n\r\nMessage: %s\r\n\r\nManufacturer: " +
                        "%s\r\nModel: %s\r\nProduct: %s\r\n\r\nAndroid Version: %s\r\nAPI Level: %d\r\nHeap Size: " +
                        "%sMB\r\n\r\nMac Address : %s\r\n\r\n" +
                        "Stack Trace:\r\n\r\n%s",
                new Date(), thread.getId(), ex.getMessage(), Build.MANUFACTURER, Build.MODEL, Build.PRODUCT,
                Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Runtime.getRuntime().maxMemory() / 1024 / 1024,
                getLocalMacAddressFromIp(), Log.getStackTraceString(ex));
        writeToFile(logMessage);
        return true;
    }

    private void writeToFile(String errMsg) {
        File dir = getDiskCacheDir(ERROR_LOG_DIR);
        dir.mkdirs();
        File log = new File(dir.getAbsolutePath() + File.separator + getFileName());
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(log, true));
            printWriter.print(errMsg);
            printWriter.print("\n\n---------------------------------------------------------------------------\n\n");
        } catch (Throwable tr2) {
            tr2.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    private String getFileName() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return df.format(new Date()) + ".txt";
    }

    /**
     * 生成日志保存路径
     *
     * @param uniqueName 日志名
     * @return 日志保存路径
     */
    private File getDiskCacheDir(String uniqueName) {
        return new File(Environment.getExternalStorageDirectory().getPath() + File.separator + uniqueName);
    }


    /**
     * 获取MAC地址
     */
    public String getLocalMacAddressFromIp() {
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            return byte2hex(mac);
        } catch (Exception e) {
            return "Unknow Mac Address";
        }
    }

    public static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp;
        for (byte aB : b) {
            stmp = Integer.toHexString(aB & 0xFF);
            if (stmp.length() == 1) {
                hs = hs.append("0").append(stmp);
            } else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            return "Unknow Ip Address";
        }
        return "Unknow Ip Address";
    }

}
