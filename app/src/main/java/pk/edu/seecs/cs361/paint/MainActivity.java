package pk.edu.seecs.cs361.paint;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout toolbar;
    private PaintCanvas paintCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add click event listeners to toolbar buttons
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            toolbar.getChildAt(i).setOnClickListener(this);
        }
        findViewById(R.id.clear).setOnClickListener(this);

        // Initialize canvas where everything is drawn
        paintCanvas = (PaintCanvas) findViewById(R.id.canvas);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintCanvas.init(metrics);

        // Select Pen tool by default
        enableButton(0);

    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (v.getId() == toolbar.getChildAt(i).getId()) {
                switch (i) {
                    case 0: // PEN
                        disableButtons();
                        enableButton(i);
                        paintCanvas.enablePen();
                        break;
                    case 1: // LINE
                        disableButtons();
                        enableButton(i);
                        paintCanvas.enableLine();
                        break;
                    case 2: // BOX
                        disableButtons();
                        enableButton(i);
                        paintCanvas.enableBox();
                        break;
                    case 3: // CIRCLE
                        disableButtons();
                        break;
                    case 4: // STROKE WIDTH

                        break;
                    case 5: // COLOR PICKER
                        // TODO: Implement color picker
                        // Display color picker here. And set color using paintCanvas.setBrushColor once a color is picked.
                        break;
                }
            }
        }

        if (v.getId() == R.id.clear) {
            paintCanvas.clear();
        }
    }

    private void setToolbarItemColor(int itemIndex, int color) {
        try {
            ((ImageButton) toolbar.getChildAt(itemIndex)).setColorFilter(color);
        } catch (Exception ignored) {

        }
    }

    private void enableButton(int toolId) {
        setToolbarItemColor(toolId, Color.parseColor("#883997"));
    }

    private void disableButtons() {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (i == 4) continue;
            setToolbarItemColor(i, Color.WHITE);
        }
    }

}