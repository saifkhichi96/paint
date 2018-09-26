package sfllhkhan95.doodle.core.models.tools

import sfllhkhan95.doodle.core.models.PaintBrush

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
object ToolFactory {

    operator fun get(type: Class<out Tool>, paintBrush: PaintBrush): Tool? {
        var tool: Tool? = null
        try {
            val ctor = type.getDeclaredConstructor(PaintBrush::class.java)
            tool = ctor.newInstance(paintBrush)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tool
    }

}
