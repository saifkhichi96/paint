package pk.edu.seecs.cs361.paint.shapes;

import pk.edu.seecs.cs361.paint.core.PaintBrush;
import pk.edu.seecs.cs361.paint.core.PaintCanvas;

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
