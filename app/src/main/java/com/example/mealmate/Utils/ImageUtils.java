package com.example.mealmate.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

public class ImageUtils {
    public static Bitmap blurBitmap(Context context, Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap outputBitmap = Bitmap.createBitmap(mutableBitmap);
        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();

        // Set the blur mask filter with a specified radius
        paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL)); // Adjust radius if needed
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);

        return outputBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static void setBlurredImage(Context context, ImageView imageView, int resourceId) {
        int reqWidth = 800;  // Example width
        int reqHeight = 600; // Example height

        Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), resourceId, reqWidth, reqHeight);

        if (bitmap != null) {
            Bitmap blurredBitmap = blurBitmap(context, bitmap);
            imageView.setImageBitmap(blurredBitmap);
        }
    }
}
