package pk.edu.seecs.cs361.paint.core;

import android.graphics.Color;
import android.graphics.Path;

/**
 * PaintObject represents a single path drawn on the canvas. Characteristics of the path, including
 * its shape, color, etc. are stored in this class.
 *
 * @author alichishti
 * @version 1.0
 * @see PaintCanvas
 */
class PaintObject {

    /**
     * The shape (or path on canvas) of the object.
     */
    private final Path path;

    private final int strokeColor;
    private final int strokeWidth;

    private final boolean filled;
    private final int fillColor;

    private int objectType = 0;

    PaintObject(int strokeColor, int strokeWidth, Path path) {
        this.path = path;

        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;

        this.filled = false;
        this.fillColor = Color.TRANSPARENT;
    }

    PaintObject(int strokeColor, int strokeWidth, Path path, boolean filled, int fillColor) {
        this.path = path;

        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;

        this.filled = filled;
        this.fillColor = fillColor;
    }

    int getStrokeColor() {
        return strokeColor;
    }

    int getStrokeWidth() {
        return strokeWidth;
    }

    boolean isFilled() {
        return filled;
    }

    int getFillColor() {
        return fillColor;
    }

    Path getPath() {
        return path;
    }

    void setDoodle() {
        objectType = 0;
    }

    boolean isDoodle() {
        return objectType == 0;
    }

    void setLine() {
        objectType = 1;
    }

    boolean isLine() {
        return objectType == 1;
    }

    void setBox() {
        objectType = 2;
    }

    boolean isBox() {
        return objectType == 2;
    }

    void setCircle() {
        objectType = 3;
    }

    boolean isCircle() {
        return objectType == 3;
    }


}
