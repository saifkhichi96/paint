package sfllhkhan95.doodle.core.models.tools;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import sfllhkhan95.doodle.core.models.PaintBrush;
import sfllhkhan95.doodle.core.models.PaintCanvas;
import sfllhkhan95.doodle.core.views.PaintView;

/**
 * Tool represents a single path drawn on the canvas. Characteristics of the path, including
 * its shape, color, etc. are stored in this class.
 *
 * @author alichishti
 * @version 1.0
 * @see PaintView
 */
public abstract class Tool extends Path {

    final PaintBrush paintBrush;

    Tool(PaintBrush paintBrush) {
        this.paintBrush = paintBrush.clone();
        this.reset();
    }

    public abstract void draw(PointF a, PointF b);

    public void paint(PaintCanvas canvas) {
        paintBrush.setStyle(Paint.Style.STROKE);
        paintBrush.setColor(paintBrush.getStrokeColor());
        paintBrush.setStrokeWidth(paintBrush.getSize());
        canvas.drawPath(this, paintBrush);

        paintBrush.setStyle(Paint.Style.FILL);
        paintBrush.setColor(paintBrush.getFillColor());
        canvas.drawPath(this, paintBrush);
    }

}
