package sfllhkhan95.doodle.core.utils;

import android.view.Menu;
import android.view.MenuItem;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.views.PaintView;

/**
 * @author saifkhichi96
 * @see CanvasActionListener
 */
public class ActionBarManager implements CanvasActionListener {

    // Action bar buttons
    private final MenuItem undoAction;
    private final MenuItem redoAction;
    private final MenuItem revertAction;

    public ActionBarManager(Menu menu) {
        undoAction = menu.findItem(R.id.undo);
        redoAction = menu.findItem(R.id.redo);
        revertAction = menu.findItem(R.id.revert);
    }

    private void enable(MenuItem item) {
        item.setEnabled(true);
    }

    private void disable(MenuItem item) {
        item.setEnabled(false);
    }

    @Override
    public void onDrawPath() {
        enable(revertAction);
        enable(undoAction);
        disable(redoAction);
    }

    @Override
    public void onUndo(boolean canUndo) {
        if (!canUndo) {
            disable(revertAction);
            disable(undoAction);
        }

        enable(redoAction);
    }

    @Override
    public void onRedo(boolean canRedo) {
        if (!canRedo) {
            disable(redoAction);
        }

        enable(revertAction);
        enable(undoAction);
    }

    @Override
    public void onRevert() {
        disable(revertAction);
        disable(undoAction);
        disable(redoAction);
    }

    public void sync(PaintView paintView) {
        if (paintView.isModified()) {
            enable(revertAction);
            enable(undoAction);
        } else {
            disable(revertAction);
            disable(undoAction);
        }

        if (paintView.canRedo()) {
            enable(redoAction);
        } else {
            disable(redoAction);
        }
    }
}
