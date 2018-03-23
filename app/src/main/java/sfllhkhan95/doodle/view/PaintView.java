package sfllhkhan95.doodle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import sfllhkhan95.doodle.core.CanvasActionListener;
import sfllhkhan95.doodle.core.PaintBrush;
import sfllhkhan95.doodle.core.PaintCanvas;
import sfllhkhan95.doodle.core.Shapes;
import sfllhkhan95.doodle.shapes.ColorPicker;
import sfllhkhan95.doodle.shapes.Eraser;
import sfllhkhan95.doodle.shapes.Shape;
import sfllhkhan95.doodle.shapes.ShapeFactory;
import sfllhkhan95.doodle.utils.OnColorPickedListener;


/**
 * @author alichishti
 */
public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 5;
    private final Shapes shapes = new Shapes();
    private final PaintBrush mBrush;

    private PaintCanvas mCanvas;
    private PointF iTouch = new PointF();
    private PointF fTouch = new PointF();
    private Class<? extends Shape> shapeType = null;

    private CanvasActionListener canvasActionListener;
    private OnColorPickedListener onColorPickedListener;

    public PaintView(Context context) {
        super(context);
        mBrush = new PaintBrush(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBrush = new PaintBrush(context);
    }

    public PaintBrush getBrush() {
        return mBrush;
    }

    public PaintCanvas getCanvas() {
        return mCanvas;
    }

    public void setCanvas(PaintCanvas canvas) {
        mCanvas = canvas;
    }

    public void setShapeType(Class<? extends Shape> shapeType) {
        this.shapeType = shapeType;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        mCanvas.drawBackground();
        mCanvas.drawShapes(shapes);

        canvas.drawBitmap(mCanvas.getBitmap(), 0, 0, null);
        canvas.restore();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (shapeType == null) return false;
        PointF touchAt = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(touchAt);
                performClick();
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                touchMove(touchAt);
                performClick();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                touchUp(touchAt);
                performClick();
                invalidate();
                return true;
        }

        return false;
    }

    private void touchStart(PointF touchAt) {
        if (this.shapeType == ColorPicker.class) {
            int color = mCanvas.getColor(touchAt);
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener.onColorPicked(color);
            }
            return;
        }

        iTouch.set(touchAt);
        fTouch = null;
    }

    private void touchMove(PointF touchAt) {
        if (this.shapeType == ColorPicker.class) {
            int color = mCanvas.getColor(touchAt);
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener.onColorPicked(color);
            }
            return;
        }


        if (fTouch == null) {
            fTouch = new PointF();

            Shape shape = ShapeFactory.get(shapeType, mBrush);
            shape.moveTo(iTouch.x, iTouch.y);
            if (shape.getClass().equals(Eraser.class)) {
                ((Eraser) shape).initEraser(mCanvas);
            }
            shapes.add(shape);
        }

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

    private void touchUp(PointF touchAt) {
        if (this.shapeType == ColorPicker.class) {
            int color = mCanvas.getColor(touchAt);
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener.onColorPicked(color);
            }
            return;
        }

        if (iTouch == null) return;
        if (fTouch == null) {
            fTouch = touchAt;
            Shape shape = ShapeFactory.get(shapeType, mBrush);
            shape.moveTo(touchAt.x, touchAt.y);
            if (shape.getClass().equals(Eraser.class)) {
                ((Eraser) shape).initEraser(mCanvas);
            }
            shapes.add(shape);
        }

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
        mCanvas.saveProject();
    }

    public void setCanvasActionListener(CanvasActionListener canvasActionListener) {
        this.canvasActionListener = canvasActionListener;
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener;
    }

    public boolean isModified() {
        return shapes.getPointer() > 0;
    }

    public boolean canRedo() {
        return shapes.getPointer() < shapes.size();
    }
}