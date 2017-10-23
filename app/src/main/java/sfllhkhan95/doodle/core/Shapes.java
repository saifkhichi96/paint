package sfllhkhan95.doodle.core;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import sfllhkhan95.doodle.shapes.Shape;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Shapes extends ArrayList<Shape> {

    private int pointer = 0;

    @Override
    public boolean add(Shape shape) {
        // Delete any undo-ed shapes
        while (this.size() > pointer) {
            this.remove(this.size() - 1);
        }

        // Add the new shape
        if (super.add(shape)) {
            pointer++;
            return true;
        }

        return false;
    }

    public boolean undo() {
        if (pointer > 0) {
            pointer--;
        }

        return pointer > 0;
    }

    public boolean redo() {
        if (pointer < this.size()) {
            pointer++;
        }

        return pointer < this.size();
    }

    @Override
    public void clear() {
        pointer = 0;
        super.clear();
    }

    void paint(PaintCanvas canvas) {
        int i = 0;
        for (Shape shape : this) {
            if (++i > pointer) break;
            shape.paint(canvas);
        }
    }

    @Nullable
    public Shape getCurrent() {
        return this.size() > 0 ? this.get(this.size() - 1) : null;
    }

}
