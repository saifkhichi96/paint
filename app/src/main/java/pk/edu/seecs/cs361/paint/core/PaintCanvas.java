package pk.edu.seecs.cs361.paint.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;


/**
 * @author saifkhichi96
 */
public class PaintCanvas extends Canvas {

    private static final int DEFAULT_BG_COLOR = Color.BLACK;
    private int bgColor = DEFAULT_BG_COLOR;

    private Bitmap bitmap;

    public PaintCanvas(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        setBitmap(bitmap);
    }

    public int getColor() {
        return bgColor;
    }

    public void setColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}