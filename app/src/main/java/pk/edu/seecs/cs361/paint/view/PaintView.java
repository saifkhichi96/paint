package pk.edu.seecs.cs361.paint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import pk.edu.seecs.cs361.paint.core.CanvasActionListener;
import pk.edu.seecs.cs361.paint.core.PaintBrush;
import pk.edu.seecs.cs361.paint.core.PaintCanvas;
import pk.edu.seecs.cs361.paint.core.Shapes;
import pk.edu.seecs.cs361.paint.shapes.Pen;
import pk.edu.seecs.cs361.paint.shapes.Shape;
import pk.edu.seecs.cs361.paint.shapes.ShapeFactory;
import pk.edu.seecs.cs361.paint.utils.DoodleDatabase;


/**
 * @author alichishti
 */
public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 5;

    private PointF iTouch = new PointF();
    private PointF fTouch = new PointF();

    private PaintBrush mBrush = new PaintBrush();
    private PaintCanvas mCanvas;

    private final Shapes shapes = new Shapes();

    private Class<? extends Shape> shapeType = Pen.class;
    private boolean ortho = true;

    private String projectName = null;
    private Bitmap savedBitmap = null;

    private CanvasActionListener canvasActionListener;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadFromBitmap(DisplayMetrics metrics, Bitmap srcBmp) {
        init(metrics);

        float deviceAspect = metrics.widthPixels / (float) metrics.heightPixels;
        float bmpAspect = srcBmp.getWidth() / (float) srcBmp.getHeight();

        if (deviceAspect != bmpAspect) {
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
        } else {
            savedBitmap = Bitmap.createScaledBitmap(srcBmp, metrics.widthPixels, metrics.heightPixels, false);
        }
    }

    public void loadFromPath(DisplayMetrics metrics, String bmpPath) {
        Bitmap srcBmp = DoodleDatabase.loadDoodle(bmpPath);
        loadFromBitmap(metrics, srcBmp);

        projectName = bmpPath;
    }

    public void init(DisplayMetrics metrics) {
        mCanvas = new PaintCanvas(metrics);
    }

    public PaintBrush getBrush() {
        return mBrush;
    }

    public PaintCanvas getCanvas() {
        return mCanvas;
    }

    public void setShapeType(Class<? extends Shape> shapeType) {
        this.shapeType = shapeType;
    }

    public boolean toggle2D() {
        ortho = !ortho;
        return ortho;
    }

    public boolean isOrtho() {
        return ortho;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        this.mCanvas.drawColor(this.mCanvas.getColor());

        if (savedBitmap != null) {
            this.mCanvas.drawBitmap(savedBitmap, 0, 0, null);
        }

        shapes.paint(mCanvas);

        canvas.drawBitmap(this.mCanvas.getBitmap(), 0, 0, null);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF touchAt = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(touchAt);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(touchAt);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }

        return true;
    }

    private void touchStart(PointF touchAt) {
        Shape shape = ShapeFactory.get(shapeType, mBrush);
        shapes.add(shape);

        iTouch.set(touchAt);
        Shape currentShape = shapes.getCurrent();
        if (currentShape != null) {
            currentShape.moveTo(touchAt.x, touchAt.y);
        }
    }

    private void touchMove(PointF touchAt) {
        float dx = Math.abs(touchAt.x - fTouch.x);
        float dy = Math.abs(touchAt.y - fTouch.y);
        if (dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE) {
            return;
        }

        fTouch.set(touchAt);
        Shape currentShape = shapes.getCurrent();
        if (currentShape != null) {
            currentShape.draw(iTouch, fTouch);
        }
    }

    private void touchUp() {
        Shape currentShape = shapes.getCurrent();
        if (currentShape != null) {
            currentShape.draw(iTouch, fTouch);
            if (canvasActionListener != null) {
                canvasActionListener.onDrawPath();
            }
        }
    }

    public void clear() {
        shapes.clear();
        invalidate();

        if (canvasActionListener != null) {
            canvasActionListener.onRevert();
        }
    }

    public void redo() {
        boolean canRedo = shapes.redo();
        invalidate();

        if (canvasActionListener != null) {
            canvasActionListener.onRedo(canRedo);
        }
    }

    public void undo() {
        boolean canUndo = shapes.undo();
        invalidate();

        if (canvasActionListener != null) {
            canvasActionListener.onUndo(canUndo);
        }
    }

    public void save() {
        if (projectName == null) {
            DoodleDatabase.saveDoodle(this.mCanvas.getBitmap());
        } else {
            DoodleDatabase.saveDoodle(this.mCanvas.getBitmap(), projectName);
        }
    }

    public void setCanvasActionListener(CanvasActionListener canvasActionListener) {
        this.canvasActionListener = canvasActionListener;
    }

}