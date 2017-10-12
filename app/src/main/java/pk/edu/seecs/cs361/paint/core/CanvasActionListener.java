package pk.edu.seecs.cs361.paint.core;

/**
 * @author saifkhichi96
 */
public interface CanvasActionListener {

    void onDrawPath();
    void onUndo(boolean allowed);
    void onRedo(boolean allowed);
    void onRevert();

}
