package sfllhkhan95.doodle.shapes;

import android.graphics.Paint;

import sfllhkhan95.doodle.core.PaintBrush;
import sfllhkhan95.doodle.core.PaintCanvas;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Eraser extends Pen {

    Eraser(PaintBrush paintBrush) {
        super(paintBrush);
    }

    public void initEraser(PaintCanvas canvas) {
        paintBrush.setStrokeColor(canvas.getColor());
    }

    @Override
    public void paint(PaintCanvas canvas) {
        paintBrush.setStyle(Paint.Style.STROKE);
        paintBrush.setColor(canvas.getColor());
        paintBrush.setStrokeWidth(paintBrush.getSize() * 2.5f);
        canvas.drawPath(this, paintBrush);
    }
}
