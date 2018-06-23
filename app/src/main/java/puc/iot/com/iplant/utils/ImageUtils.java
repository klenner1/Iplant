package puc.iot.com.iplant.utils;

import android.graphics.Bitmap;
import android.util.Log;

public final class ImageUtils {

    private static final String TAG = "ImageUtils";

    public static Bitmap decodeImageFile(Bitmap bitmap) {
        int width=100, height=100;
        try {
            int scale = 1;
            while (bitmap.getWidth() / scale / 2 >= width
                    && bitmap.getHeight()/ scale / 2 >= height) {
                scale *= 2;
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth()/scale,
                    bitmap.getHeight()/scale);
            return bitmap; // Return the Bitmap
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}
