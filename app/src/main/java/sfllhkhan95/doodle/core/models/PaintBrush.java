package sfllhkhan95.doodle.core.models;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class PaintBrush extends Paint implements Cloneable {

    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_FILL_COLOR = Color.BLACK;
    private static final int DEFAULT_SIZE = 10;

    private int strokeColor;
    private int fillColor;
    private int size = DEFAULT_SIZE;

    public PaintBrush(Context context) {
        strokeColor = DEFAULT_STROKE_COLOR;
        fillColor = DEFAULT_FILL_COLOR;

        this.setColor(strokeColor);
        this.setStyle(Paint.Style.STROKE);
        this.setStrokeJoin(Paint.Join.ROUND);
        this.setStrokeCap(Paint.Cap.ROUND);
        this.setXfermode(null);
        this.setAlpha(0xff);
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = DEFAULT_SIZE + size;
    }

    @Override
    public PaintBrush clone() {
        try {
            return (PaintBrush) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }
}
