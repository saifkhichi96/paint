package sfllhkhan95.doodle.shapes;

import sfllhkhan95.doodle.core.PaintBrush;
import sfllhkhan95.doodle.core.PaintCanvas;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Eraser extends Pen {

    public Eraser(PaintBrush paintBrush) {
        super(paintBrush);
    }

    public void initEraser(PaintCanvas canvas) {
        paintBrush.setFillColor(canvas.getColor());
        paintBrush.setStrokeColor(canvas.getColor());
    }

}
