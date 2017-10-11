package pk.edu.seecs.cs361.paint;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import pk.edu.seecs.cs361.paint.core.PaintCanvas;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout toolbar;
    private int selectedTool = -1;

    private PaintCanvas paintCanvas;

    private AlertDialog confirmClear;
    private AlertDialog confirmSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create confirmation dailogs
        confirmClear = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("This action will erase everything drawn on canvas. It cannot be reversed. Do you really wish to proceed?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintCanvas.clear();
                        confirmClear.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmClear.dismiss();
                    }
                })
                .create();

        confirmSave = new AlertDialog.Builder(this)
                .setTitle("Save doodle to Galley?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintCanvas.save();
                        confirmClear.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmClear.dismiss();
                    }
                })
                .create();

        // Add click event listeners to toolbar buttons
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            toolbar.getChildAt(i).setOnClickListener(this);
        }
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.undo).setOnClickListener(this);
        findViewById(R.id.redo).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        // Initialize canvas where everything is drawn

        // Initialize canvas
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        paintCanvas = (PaintCanvas) findViewById(R.id.canvas);
        String savedDoodle = getIntent().getStringExtra("DOODLE");
        if (savedDoodle != null && !savedDoodle.equals("")) {
            paintCanvas.init(metrics, savedDoodle);
        }

        paintCanvas = (PaintCanvas) findViewById(R.id.canvas);
        Intent data = getIntent().getParcelableExtra("FROM_GALLERY");
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            paintCanvas.init(metrics, BitmapFactory.decodeFile(picturePath));
        } else {
            paintCanvas.init(metrics);
            paintCanvas.setCanvasColor(getIntent().getIntExtra("BG_COLOR", Color.BLACK));
        }

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
                        if (selectedTool == 2) {
                            if (paintCanvas.toggle3D()) {
                                ((ImageButton) toolbar.getChildAt(i)).setImageResource(R.drawable.ic_box);
                            } else {
                                ((ImageButton) toolbar.getChildAt(i)).setImageResource(R.drawable.ic_square);
                            }
                        }

                        disableButtons();
                        enableButton(i);
                        paintCanvas.enableBox();
                        break;
                    case 3: // CIRCLE
                        disableButtons();
                        enableButton(i);
                        paintCanvas.enableCircle();
                        break;
                    case 4: // STROKE WIDTH
                        if (paintCanvas.toggleFilled()) {
                            ((ImageButton) toolbar.getChildAt(i)).setImageResource(R.drawable.ic_fill);
                        } else {
                            ((ImageButton) toolbar.getChildAt(i)).setImageResource(R.drawable.ic_fill_none);
                        }
                        break;
                    case 5: // COLOR PICKER
                        // TODO: Implement color picker
                        // Display color picker here. And set color using paintCanvas.setBrushColor once a color is picked.
                        break;
                }
                selectedTool = i;
            }
        }

        switch (v.getId()) {
            case R.id.clear:
                confirmClear.show();
                break;

            case R.id.undo:
                paintCanvas.undo();
                if (paintCanvas.canRedo()) {
                    findViewById(R.id.redo).setVisibility(View.VISIBLE);
                }
                break;

            case R.id.redo:
                paintCanvas.redo();
                if (!paintCanvas.canRedo()) {
                    findViewById(R.id.redo).setVisibility(View.GONE);
                }
                break;

            case R.id.save:
                confirmSave.show();
                break;
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