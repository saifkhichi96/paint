package pk.edu.seecs.cs361.paint.utils;

import android.view.Menu;
import android.view.MenuItem;

import pk.edu.seecs.cs361.paint.R;
import pk.edu.seecs.cs361.paint.core.CanvasActionListener;

/**
 * @author saifkhichi96
 * @see pk.edu.seecs.cs361.paint.core.CanvasActionListener
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

}
