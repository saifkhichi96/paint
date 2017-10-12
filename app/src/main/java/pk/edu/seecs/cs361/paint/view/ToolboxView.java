package pk.edu.seecs.cs361.paint.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * @author saifkhichi96
 */
public class ToolboxView extends LinearLayout {
    private int selectedTool = -1;
    private ArrayList<Integer> nonSticky = new ArrayList<>();

    public ToolboxView(Context context) {
        super(context);
    }

    public ToolboxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolboxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener l) {
        for (int i = 0; i < this.getChildCount(); i++) {
            this.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < getChildCount(); i++) {
                        if (v.getId() == getChildAt(i).getId()) {
                            if (l != null) {
                                l.onClick(v);
                            }
                            selectTool(i);
                        }
                    }
                }
            });
        }
    }

    private void setToolColor(int itemIndex, int color) {
        try {
            ((ImageButton) this.getChildAt(itemIndex)).setColorFilter(color);
        } catch (Exception ignored) {

        }
    }

    public void addUnselectable(int id) {
        nonSticky.add(id);
    }

    public void selectTool(int id) {
        if (nonSticky.contains(id)) return;

        deselectAll();
        selectedTool = id;
        setToolColor(id, Color.parseColor("#883997"));
    }

    private void deselectAll() {
        selectedTool = -1;
        for (int i = 0; i < this.getChildCount(); i++) {
            setToolColor(i, Color.WHITE);
        }
    }

    public int getSelectedTool() {
        return selectedTool;
    }
}
