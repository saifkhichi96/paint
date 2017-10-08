package pk.edu.seecs.cs361.paint;

/**
 * Created by ALI CHISHTI on 10/8/2017.
 */
import android.graphics.Path;

public class FingerPath {

    public int color;
    public int strokeWidth;
    public Path path;

    public FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
