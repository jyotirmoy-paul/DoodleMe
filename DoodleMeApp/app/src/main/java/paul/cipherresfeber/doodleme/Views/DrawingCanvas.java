package paul.cipherresfeber.doodleme.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingCanvas extends View {

    private int backgroundColor = Color.WHITE;
    private float strokeWidth = 5f;

    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public DrawingCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    public void init(DisplayMetrics displayMetrics) {
        mBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setMaskFilter(null);
            mCanvas.drawPath(fp.path, mPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchDown(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(mPath);
        paths.add(fp);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        float touchVariance = 2;
        if (dx >= touchVariance || dy >= touchVariance) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        // get the initial touch points
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchDown(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    // method for clearing the drawing canvas
    public void clear() {
        backgroundColor = Color.WHITE;
        paths.clear();
        invalidate();
    }

    // method that returns the bitmap of drawn image on canvas - 28 x 28
    public int[] getPixelData(){
        Bitmap bitmap = resizeBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // get the pixel data from bitmap
        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    // this method is made to resize the final image to 28x28
    public Bitmap resizeBitmap(){

        Bitmap temp;

        // first down sample the image to 800 x 800 pixels
        temp = getResizedBitmapWithAdjustment(mBitmap, 800);

        // down sample the image to 400 pixels
        temp = getResizedBitmapWithAdjustment(temp, 400);

        // down sample the image to 200 pixels
        temp = getResizedBitmapWithAdjustment(temp, 200);

        // down sample the image to 100 pixel
        return getResizedBitmapWithAdjustment(temp, 100);/*

        // down sample the image to 50 pixel
        temp = getResizedBitmapWithAdjustment(temp, 50);

        // finally return the 28x28 pixel bitmap
        return getResizedBitmapWithAdjustment(temp, 28);*/
    }

    private Bitmap getResizedBitmapWithAdjustment(Bitmap b, int size){

        // down-sampled the bitmap
        Bitmap tempBitmap = Bitmap.createScaledBitmap(b, size, size, false);

        // converted the bitmap to array of pixels
        int[] arr = new int[size*size];
        tempBitmap.getPixels(arr, 0, size, 0, 0, size, size);

        int WHITE = -1;
        int BLACK = -16777216;

        // polishing the pixel values
        for(int i=0; i<arr.length; i++){
            if(arr[i] != WHITE){
                arr[i] = BLACK;
            }
        }

        // creating bitmap from polished pixel values
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(arr, 0, size, 0, 0, size, size);
        return bitmap;
    }

}
