package sfllhkhan95.doodle.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;

import sfllhkhan95.doodle.utils.DoodleDatabase;


/**
 * @author saifkhichi96
 */
public class PaintCanvas extends Canvas {

    private static final int DEFAULT_BG_COLOR = Color.BLACK;
    private int bgColor = DEFAULT_BG_COLOR;

    private Bitmap bgImage;
    private Bitmap bitmap;
    private String projectName = null;

    public static PaintCanvas loadFromBitmap(DisplayMetrics metrics, Bitmap srcBmp) {
        PaintCanvas canvas = new PaintCanvas(metrics);

        float deviceAspect = metrics.widthPixels / (float) metrics.heightPixels;
        float bmpAspect = srcBmp.getWidth() / (float) srcBmp.getHeight();

        if (deviceAspect != bmpAspect) {
            if (srcBmp.getWidth() >= srcBmp.getHeight()) {
                srcBmp = Bitmap.createBitmap(
                        srcBmp,
                        srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                        0,
                        srcBmp.getHeight(),
                        srcBmp.getHeight()
                );
            } else {
                srcBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                        srcBmp.getWidth(),
                        srcBmp.getWidth()
                );
            }

            if (metrics.widthPixels > metrics.heightPixels) {
                int w = metrics.widthPixels;
                int h = (int) (metrics.widthPixels * srcBmp.getHeight() / (float) srcBmp.getWidth());
                canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, w, h, false);
            } else {
                int h = metrics.heightPixels;
                int w = (int) (metrics.heightPixels * srcBmp.getWidth() / (float) srcBmp.getHeight());
                canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, w, h, false);
            }
        } else {
            canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, metrics.widthPixels, metrics.heightPixels, false);
        }

        return canvas;
    }

    public static PaintCanvas loadFromPath(DisplayMetrics metrics, String bmpPath) {
        Bitmap srcBmp = DoodleDatabase.loadDoodle(bmpPath, metrics.widthPixels, metrics.heightPixels);
        PaintCanvas canvas = loadFromBitmap(metrics, srcBmp);
        canvas.setProjectName(bmpPath);

        return canvas;
    }

    public PaintCanvas(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
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

    public void drawShapes(Shapes shapes) {
        shapes.paint(this);
    }

    public void drawBackground() {
        if (bgImage == null) {
            drawColor(bgColor);
        } else {
            drawBitmap(bgImage, 0, 0, null);
        }
    }

    private void setProjectName(String title) {
        this.projectName = title;
    }

    public void saveProject() {
        if (projectName == null) {
            DoodleDatabase.saveDoodle(bitmap);
        } else {
            DoodleDatabase.saveDoodle(bitmap, projectName);
        }
    }

}