package pk.edu.seecs.cs361.paint.shapes;

import android.graphics.PointF;

import pk.edu.seecs.cs361.paint.core.PaintBrush;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Quad2D extends Shape {

    public Quad2D(PaintBrush paintBrush) {
        super(paintBrush);
    }

    @Override
    public void draw(PointF i, PointF f) {
        this.reset();
        this.moveTo(i.x, i.y);
        this.lineTo(i.x, f.y);
        this.lineTo(f.x, f.y);
        this.lineTo(f.x, i.y);
        this.lineTo(i.x, i.y);
    }
}
