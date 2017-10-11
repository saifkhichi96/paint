package pk.edu.seecs.cs361.paint.core;

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

import pk.edu.seecs.cs361.paint.utils.DoodleDatabase;


/**
 * @author alichishti
 */
public class PaintCanvas extends View {

    private static final int DEFAULT_BG_COLOR = Color.BLACK;
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_STROKE_WIDTH = 10;

    private static final float TOUCH_TOLERANCE = 10;

    private final Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private final ArrayList<PaintObject> paths = new ArrayList<>();
    private int pointer = 0;

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;
    private Path mPath;
    private float iX, iY;
    private float mX, mY;

    private int canvasColor = DEFAULT_BG_COLOR;
    private int brushColor = DEFAULT_COLOR;
    private int strokeWidth = DEFAULT_STROKE_WIDTH;

    private boolean pen = true;
    private boolean line = false;
    private boolean box = false;
    private boolean circle = false;

    private boolean three3d = false;

    private int fillColor = DEFAULT_COLOR;
    private boolean filled = false;

    private String projectName = null;
    private Bitmap savedBitmap = null;

    public PaintCanvas(Context context) {
        this(context, null);
        mPaint = new Paint();
        mPaint.setColor(brushColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
    }

    public PaintCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(brushColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

    }

    public void init(DisplayMetrics metrics, Bitmap srcBmp) {
        init(metrics);

        if (srcBmp.getWidth() >= srcBmp.getHeight()) {
            srcBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );
        } else {
            srcBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        if (metrics.widthPixels > metrics.heightPixels) {
            int w = metrics.widthPixels;
            int h = (int) (metrics.widthPixels * srcBmp.getHeight() / (float) srcBmp.getWidth());
            savedBitmap = Bitmap.createScaledBitmap(srcBmp, w, h, false);
        } else {
            int h = metrics.heightPixels;
            int w = (int) (metrics.heightPixels * srcBmp.getWidth() / (float) srcBmp.getHeight());
            savedBitmap = Bitmap.createScaledBitmap(srcBmp, w, h, false);
        }
    }

    public void init(DisplayMetrics metrics, String doodlePath) {
        init(metrics);

        Bitmap savedDoodle = DoodleDatabase.loadDoodle(doodlePath);
        savedBitmap = savedDoodle.copy(Bitmap.Config.ARGB_8888, true);

        projectName = doodlePath;
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

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public void setCanvasColor(int canvasColor) {
        this.canvasColor = canvasColor;
    }

    public void enablePen() {
        this.pen = true;

        this.line = false;
        this.box = false;
        this.circle = false;
    }

    public void enableLine() {
        this.line = true;

        this.pen = false;
        this.box = false;
        this.circle = false;
    }

    public void enableBox() {
        this.box = true;

        this.pen = false;
        this.line = false;
        this.circle = false;
    }

    public void enableCircle() {
        this.circle = true;

        this.box = false;
        this.pen = false;
        this.line = false;
    }

    public boolean toggle3D() {
        three3d = !three3d;
        return three3d;
    }

    public boolean toggleFilled() {
        this.filled = !this.filled;
        return this.filled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(canvasColor);

        if (savedBitmap != null) {
            mCanvas.drawBitmap(savedBitmap, 0, 0, mPaint);
        }

        int i = 0;
        for (PaintObject fp : paths) {
            if (++i > pointer) break;

            if (fp.isFilled() && !fp.isLine()) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(fp.getFillColor());
            } else {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(fp.getStrokeColor());
            }

            mPaint.setStrokeWidth(fp.getStrokeWidth());
            mCanvas.drawPath(fp.getPath(), mPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    public void clear() {
        pointer = 0;
        paths.clear();
        invalidate();
    }

    public boolean canRedo() {
        return pointer < paths.size();
    }

    public void redo() {
        if (pointer < paths.size()) {
            pointer++;
            invalidate();
        }
    }

    public boolean canUndo() {
        return pointer > 0;
    }

    public void undo() {
        if (pointer > 0) {
            pointer--;
            invalidate();
        }
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

        // Delete any undo-ed paths
        while (paths.size() > pointer) {
            paths.remove(paths.size() - 1);
        }

        PaintObject fp = new PaintObject(brushColor, strokeWidth, mPath, filled, fillColor);
        pointer++;
        fp.setDoodle();

        if (line) {
            fp.setLine();
        } else if (box) {
            fp.setBox();
        } else if (circle) {
            fp.setCircle();
        }

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
            mX = x;
            mY = y;
        }

        if (pen) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        } else if (line) {
            drawLine(iX, iY, mX, mY);
        } else if (box) {
            drawRect(iX, iY, mX, mY);
        } else if (circle) {
            drawCircle(iX, iY, mX, mY);
        }
    }

    private void touchUp() {
        if (pen) {
            mPath.lineTo(mX, mY);
        } else if (line) {
            drawLine(iX, iY, mX, mY);
        } else if (box) {
            drawRect(iX, iY, mX, mY);
        } else if (circle) {
            drawCircle(iX, iY, mX, mY);
        }
    }

    private void drawRect(float iX, float iY, float fX, float fY) {
        mPath.reset();
        mPath.moveTo(iX, iY);

        if (!three3d) {
            mPath.lineTo(iX, fY);
            mPath.lineTo(fX, fY);
            mPath.lineTo(fX, iY);
            mPath.lineTo(iX, iY);
        } else {
            // Front
            mPath.lineTo(iX, fY);
            mPath.lineTo(fX, fY);
            mPath.lineTo(fX, iY);
            mPath.lineTo(iX, iY);

            // Back
            mPath.lineTo(iX + Math.abs(fX - iX) / 2, iY + Math.abs(fY - iY) / 2);
            mPath.lineTo(iX + Math.abs(fX - iX) / 2, fY + Math.abs(fY - iY) / 2);
            mPath.lineTo(fX + Math.abs(fX - iX) / 2, fY + Math.abs(fY - iY) / 2);
            mPath.lineTo(fX + Math.abs(fX - iX) / 2, iY + Math.abs(fY - iY) / 2);
            mPath.lineTo(iX + Math.abs(fX - iX) / 2, iY + Math.abs(fY - iY) / 2);

            // Left
            mPath.lineTo(iX + Math.abs(fX - iX) / 2, fY + Math.abs(fY - iY) / 2);
            mPath.lineTo(iX, fY);

            // Right
            mPath.lineTo(fX, fY);
            mPath.lineTo(fX + Math.abs(fX - iX) / 2, fY + Math.abs(fY - iY) / 2);
            mPath.lineTo(fX + Math.abs(fX - iX) / 2, iY + Math.abs(fY - iY) / 2);
            mPath.lineTo(fX, iY);
        }
    }

    private void drawLine(float iX, float iY, float fX, float fY) {
        mPath.reset();
        mPath.moveTo(iX, iY);
        mPath.lineTo(fX, fY);
    }

    private void drawCircle(float iX, float iY, float fX, float fY) {
        // Find midpoint
        float x = (iX + fX) / 2;
        float y = (iY + fY) / 2;

        // Calculate radius
        float xr = Math.abs((fX - iX) / 2);
        float yr = Math.abs((fY - iY) / 2);
        float r = (xr > yr) ? xr : yr;

        // Add circle to path
        mPath.reset();
        mPath.moveTo(iX, iY);
        mPath.addCircle(x, y, r, Path.Direction.CW);
    }

    public void save() {
        if (projectName == null) {
            DoodleDatabase.saveDoodle(mBitmap);
        } else {
            DoodleDatabase.saveDoodle(mBitmap, projectName);
        }
    }

}