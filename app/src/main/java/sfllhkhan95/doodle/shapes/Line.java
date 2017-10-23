package sfllhkhan95.doodle.shapes;

import android.graphics.PointF;

import sfllhkhan95.doodle.core.PaintBrush;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Line extends Shape {

    Line(PaintBrush paintBrush) {
        super(paintBrush);
    }

    @Override
    public void draw(PointF i, PointF f) {
        this.reset();
        this.moveTo(i.x, i.y);
        this.lineTo(f.x, f.y);
    }

}
