package pk.edu.seecs.cs361.paint;

import android.graphics.Path;

/**
 *
 *
 * @author alichishti
 */
class FingerPath {

    private int color;
    private int strokeWidth;
    private Path path;

    FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

}
