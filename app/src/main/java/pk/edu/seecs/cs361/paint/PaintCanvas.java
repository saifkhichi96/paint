package pk.edu.seecs.cs361.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


/**
 * @author alichishti
 */
public class PaintCanvas extends View {

    private static final int DEFAULT_BG_COLOR = Color.BLACK;
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_STROKE_WIDTH = 10;

    private static final float TOUCH_TOLERANCE = 10;

    private ArrayList<FingerPath> paths = new ArrayList<>();
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;
    private Path mPath;
    private float iX, iY;
    private float mX, mY;

    private int strokeWidth = DEFAULT_STROKE_WIDTH;
    private int brushColor = DEFAULT_COLOR;

    private boolean pen = true;
    private boolean line = false;
    private boolean box = false;

    public PaintCanvas(Context context) {
        this(context, null);
        mPaint = new Paint();
        mPaint.setColor(brushColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
    }

    public PaintCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(brushColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        strokeWidth = DEFAULT_STROKE_WIDTH;
        brushColor = DEFAULT_COLOR;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        mPaint.setStrokeWidth(strokeWidth);
    }

    public void setBrushColor(int brushColor) {
        this.brushColor = brushColor;
        mPaint.setColor(brushColor);
    }

    public void enablePen() {
        this.pen = true;

        this.line = false;
        this.box = false;
    }

    public void enableLine() {
        this.line = true;

        this.pen = false;
        this.box = false;
    }

    public void enableBox() {
        this.box = true;

        this.pen = false;
        this.line = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(DEFAULT_BG_COLOR);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.getColor());
            mPaint.setStrokeWidth(fp.getStrokeWidth());
            mCanvas.drawPath(fp.getPath(), mPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    public void clear() {
        paths.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }

        return true;
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(brushColor, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        iX = mX = x;
        iY = mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        if (!pen) {
            mPath.reset();
            mPath.moveTo(iX, iY);
        }

        if (pen || line) {
            mPath.lineTo(mX, mY);
        } else if (box) {
            mPath.moveTo(iX, iY);
            mPath.lineTo(iX, mY);
            mPath.lineTo(mX, mY);
            mPath.lineTo(mX, iY);
            mPath.lineTo(iX, iY);
        }
    }
}
