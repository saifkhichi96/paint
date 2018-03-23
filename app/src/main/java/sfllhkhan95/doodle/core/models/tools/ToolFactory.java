package sfllhkhan95.doodle.core.models.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import sfllhkhan95.doodle.core.models.PaintBrush;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class ToolFactory {

    public static Tool get(Class<? extends Tool> type, PaintBrush paintBrush) {
        Tool tool = null;
        try {
            Constructor<? extends Tool> ctor = type.getDeclaredConstructor(PaintBrush.class);
            tool = ctor.newInstance(paintBrush);
        } catch (NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return tool;
    }

}
