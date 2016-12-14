package cn.alexwan.ccamera.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;


/**
 * BitmapUtil
 * Created by alexwan on 2016/12/13.
 */
public class BitmapUtil {

    /**
     * Convert {@link Bitmap} to {@link String}
     *
     * @param bitmap bitmap
     * @return String
     */
    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }


    /**
     * Convert bitmap String to ByteArray
     *
     * @param bitmapByteString bitmapByteString
     * @return byte[]
     */
    public static byte[] convertBitmapStringToByteArray(String bitmapByteString) {
        return Base64.decode(bitmapByteString, Base64.DEFAULT);
    }


    /**
     * If picture's rotate not correct , rotate it.
     *
     * @param context  context
     * @param rotation rotation
     * @param data     data
     * @return Bitmap
     */
    public static Bitmap rotatePicture(Context context, int rotation, byte[] data) {
        Bitmap bitmap = decodeSampledBitmapFromByte(context, data);
        if (rotation != 0) {
            Bitmap oldBitmap = bitmap;
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false);
            oldBitmap.recycle();
        }
        return bitmap;
    }

    /**
     * Decode and sample down a bitmap from a file path
     * @param path path
     * @param reqWidth reqWidth
     * @param reqHeight reqHeight
     * @return {@link Bitmap}
     */
    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inTargetDensity = reqWidth * options.inSampleSize;
        options.inJustDecodeBounds = false;
        options.inInputShareable = true;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Decode and sample down a bitmap from a byte stream
     * @param context context
     * @param bitmapBytes bitmapBytes
     * @return {@link Bitmap}
     */
    public static Bitmap decodeSampledBitmapFromByte(Context context, byte[] bitmapBytes) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int reqWidth, reqHeight;
        // Calculate reqWidth and reqHeight
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        reqWidth = dm.widthPixels;
        reqHeight = dm.heightPixels;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Load & resize the image to be 1/inSampleSize dimensions
        // Use when you do not want to scale the image width a inSampleSize that is a power of 2.
        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inTargetDensity = reqWidth * options.inSampleSize;

        // Decode bitmap with inSample
        // If set to true , the decoder will return null (no bitmap), but the out... fields will still
        // be set , allowing the caller to query the bitmap without having to allocate the memory
        // for its pixel.
        options.inJustDecodeBounds = false;
        // Tell to gc that whether it needs free memory , the bitmap can be cleared
        options.inPurgeable = true;
        // Which kind of reference will be used to recover the Bitmap data after being clear , when
        // it will be used in the future.
        options.inInputShareable = true;
        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
    }

    /**
     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object
     * when decoding bitmaps using the decode* method from {@link BitmapFactory}. This implementation
     * calculates the closest inSampleSize that is a power of 2 and will result in the final decoded
     * bitmap having a width and height equal to or larger than the requested width and height.
     *
     * The function rounds up the sample size to a power of 2 or multiple of 8 because BitmapFactory
     * only honors sample size this way. For example, BitmapFactory down samples an image by 2 even
     * though the request is 3, So we round up the sample size to avoid OOM.
     * @param options options
     * @param reqWidth reqWidth
     * @param reqHeight reqHeight
     * @return inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int initialInSampleSize = computeInitialSampleSize(options, reqWidth, reqHeight);
        int roundedInSampleSize;
        if (initialInSampleSize <= 8) {
            roundedInSampleSize = 1;
            while (roundedInSampleSize < initialInSampleSize) {
                // Shift one bit to left
                roundedInSampleSize <<= 1;
            }
        } else {
            roundedInSampleSize = (initialInSampleSize + 7) / 8 * 8;
        }
        return roundedInSampleSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final double height = options.outHeight;
        final double width = options.outWidth;

        final long maxNumOfPixels = reqWidth * reqHeight;
        final long minSideLength = Math.min(reqHeight, reqWidth);

        int lowerBound = (maxNumOfPixels < 0) ? 1 : (int) Math.ceil(Math.sqrt(width * height / maxNumOfPixels));
        int upperBound = (minSideLength < 0) ? 128 : (int) Math.min(Math.floor(width / minSideLength), Math.floor(height / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if (maxNumOfPixels < 0 && minSideLength < 0) {
            return 1;
        } else if (minSideLength < 0) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Uri saveToSquarePicture(Context context , Bitmap bitmap){
        int cropHeight;
        if(bitmap.getHeight() > bitmap.getWidth()){
            cropHeight = bitmap.getWidth();
        } else {
            cropHeight = bitmap.getHeight();
        }

        bitmap = ThumbnailUtils.extractThumbnail(bitmap , cropHeight , cropHeight , OPTIONS_RECYCLE_INPUT);
        // Open or create a new picture dir
        File mediaStorageDir = new File(getExternalStoragePublicDirectory(DIRECTORY_PICTURES) , "ccamera");
        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            return null;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss" , Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        // Saving the bitmap
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            FileOutputStream stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
            stream.close();
        } catch (Exception e) {
            LogUtils.e("BitmapUtil" , "saveToSquarePicture : error " , e.getMessage());
        }

        // Media Scanner need to scan for the image saved
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri fileContentUri = Uri.fromFile(mediaFile);
        mediaScannerIntent.setData(fileContentUri);
        context.sendBroadcast(mediaScannerIntent);
        return fileContentUri;
    }
}
