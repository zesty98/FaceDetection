package com.gomdev.facedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by gomdev on 15. 11. 23..
 */
public class DetectionView extends ImageView {
    private static final String TAG = "DetectionView";

    private Context mContext = null;

    private int mImageWidth, mImageHeight;
    private int mNumberOfFace = 10;
    private FaceDetector faceDetector;
    private FaceDetector.Face[] faces;
    float mEyesDistance;
    int mNumberOfFaceDetected;

    Bitmap mBitmap;

    public DetectionView(Context context) {
        super(context);

        init(context);
    }

    public DetectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        mContext = context;

        BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face3, BitmapFactoryOptionsbfo);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float imageRatio = (float) width / height;

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        float screenRatio = (float) screenWidth / screenHeight;

        int dstWidth = 0;
        int dstHeight = 0;
        if (imageRatio > screenRatio) { // screenWidth : imageWidth = x : imageHeight
            dstWidth = screenWidth; // dstWidth : screenWidth  = dstHeight : (screenWidth / imageRatio)
            dstHeight = (int) ((float) dstWidth * ((float) screenWidth / imageRatio) / screenWidth);
        } else { // screenHeight : imageHeight = x : imageWidth
            dstHeight = screenHeight; // dstHeight : screenHeight = dstWidth : screenWidth;
            dstWidth = (int) ((float) dstHeight * ((float) screenHeight * imageRatio) / screenHeight);
        }

        mBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
        bitmap.recycle();

        mImageWidth = mBitmap.getWidth();
        mImageHeight = mBitmap.getHeight();

        Log.d(TAG, "DetectionView() Bitmap width=" + mImageWidth + " height=" + mImageHeight);

        faces = new FaceDetector.Face[mNumberOfFace];
        faceDetector = new FaceDetector(mImageWidth, mImageHeight, mNumberOfFace);
        mNumberOfFaceDetected = faceDetector.findFaces(mBitmap, faces);

        Log.d(TAG, "DetectionView() numOffaceDetected=" + mNumberOfFaceDetected);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        Log.d(TAG, "onDraw()");
        for (int i = 0; i < mNumberOfFaceDetected; i++) {
            FaceDetector.Face face = faces[i];
            PointF midPoint = new PointF();
            face.getMidPoint(midPoint);

            Log.d(TAG, "\tmidPoint=(" + midPoint.x + ", " + midPoint.y + ")");
            mEyesDistance = face.eyesDistance();
            Log.d(TAG, "\teyesDistance=" + mEyesDistance);

            canvas.drawRect((int) (midPoint.x - mEyesDistance * 2),
                    (int) (midPoint.y - mEyesDistance * 2),
                    (int) (midPoint.x + mEyesDistance * 2),
                    (int) (midPoint.y + mEyesDistance * 2), paint);
        }
    }
}
