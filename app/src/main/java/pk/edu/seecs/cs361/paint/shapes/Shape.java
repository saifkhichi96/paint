package pk.edu.seecs.cs361.paint.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import pk.edu.seecs.cs361.paint.core.PaintBrush;
import pk.edu.seecs.cs361.paint.view.PaintView;

/**
 * Shape represents a single path drawn on the canvas. Characteristics of the path, including
 * its shape, color, etc. are stored in this class.
 *
 * @author alichishti
 * @version 1.0
 * @see PaintView
 */
public abstract class Shape extends Path {

    private final PaintBrush paintBrush;

    public Shape(PaintBrush paintBrush) {
        this.paintBrush = paintBrush.clone();
        this.reset();
    }

    public abstract void draw(PointF a, PointF b);

    public void paint(Canvas canvas) {
        paintBrush.setStyle(Paint.Style.STROKE);
        paintBrush.setColor(paintBrush.getStrokeColor());
        paintBrush.setStrokeWidth(paintBrush.getSize());
        canvas.drawPath(this, paintBrush);

        if (paintBrush.isFilled()) {
            paintBrush.setStyle(Paint.Style.FILL);
            paintBrush.setColor(paintBrush.getFillColor());
            canvas.drawPath(this, paintBrush);
        }
    }

}
