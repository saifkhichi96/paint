package sfllhkhan95.doodle;

import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;

import sfllhkhan95.doodle.core.PaintCanvas;
import sfllhkhan95.doodle.shapes.Circle;
import sfllhkhan95.doodle.shapes.Eraser;
import sfllhkhan95.doodle.shapes.Line;
import sfllhkhan95.doodle.shapes.Pen;
import sfllhkhan95.doodle.shapes.Quad2D;
import sfllhkhan95.doodle.shapes.Quad3D;
import sfllhkhan95.doodle.utils.ActionBarManager;
import sfllhkhan95.doodle.view.PaintView;
import sfllhkhan95.doodle.view.ToolboxView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // A custom OpenGL ES canvas to draw on
    private PaintView paintView;

    // Toolbox contains the drawing tools
    private ToolboxView toolbox;

    // Dialog boxes to confirm certain permanent actions (i.e. revert and save)
    private AlertDialog revertConfirmation;
    private AlertDialog saveConfirmation;
    private AlertDialog exitConfirmation;

    private boolean fullScreen = false;
    private View actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main);

        actionBar = findViewById(R.id.actionBar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("");

        final SeekBar brushController = (SeekBar) findViewById(R.id.brushController);
        brushController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.equals(brushController)) {
                    paintView.getBrush().setSize(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Create confirmation dailogs
        revertConfirmation = new AlertDialog.Builder(this)
                .setTitle("Revert to original?")
                .setMessage("This action will erase everything drawn on canvas. It cannot be reversed. Do you really wish to proceed?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintView.clear();
                        revertConfirmation.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        revertConfirmation.dismiss();
                    }
                })
                .create();

        saveConfirmation = new AlertDialog.Builder(this)
                .setTitle("Save project to galley?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintView.save();
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveConfirmation.dismiss();
                    }
                })
                .create();

        exitConfirmation = new AlertDialog.Builder(this)
                .setTitle("Exit without saving?")
                .setMessage("This project has unsaved changes. Do you really wish to proceed?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitConfirmation.dismiss();
                    }
                })
                .create();

        // Add click event listeners to toolbox buttons
        toolbox = (ToolboxView) findViewById(R.id.toolbox);
        toolbox.addUnselectable(4);
        toolbox.setOnClickListener(this);

        findViewById(R.id.penColorPicker).setOnClickListener(this);
        findViewById(R.id.fillColorPicker).setOnClickListener(this);
        findViewById(R.id.fullScreen).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        // Initialize canvas where everything is drawn
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        PaintCanvas canvas;
        paintView = (PaintView) findViewById(R.id.canvas);
        String savedDoodle = getIntent().getStringExtra("DOODLE");
        Intent galleryImage = getIntent().getParcelableExtra("FROM_GALLERY");
        if (savedDoodle != null && !savedDoodle.equals("")) {
            canvas = PaintCanvas.loadFromPath(metrics, savedDoodle);
        } else if (galleryImage != null) {
            Uri selectedImage = galleryImage.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            canvas = PaintCanvas.loadFromBitmap(metrics, BitmapFactory.decodeFile(picturePath));
        } else {
            canvas = new PaintCanvas(metrics);
            canvas.setColor(getIntent().getIntExtra("BG_COLOR", Color.BLACK));
        }
        paintView.setCanvas(canvas);

        // Select Pen tool by default
        toolbox.selectTool(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        ActionBarManager actionBarManager = new ActionBarManager(menu);
        paintView.setCanvasActionListener(actionBarManager);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                paintView.undo();
                return true;

            case R.id.redo:
                paintView.redo();
                return true;

            case R.id.revert:
                revertConfirmation.show();
                return true;

            case R.id.eraser:
                toolbox.deselectAll();
                paintView.setShapeType(Eraser.class);
                return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < toolbox.getChildCount(); i++) {
            if (v.getId() == toolbox.getChildAt(i).getId()) {
                switch (i) {
                    case 0: // PEN
                        paintView.setShapeType(Pen.class);
                        break;
                    case 1: // LINE
                        paintView.setShapeType(Line.class);
                        break;
                    case 2: // BOX
                        if (toolbox.getSelectedTool() == 2) {
                            if (paintView.toggle2D()) {
                                ((ImageButton) toolbox.getChildAt(i)).setImageResource(R.drawable.ic_quad2d);
                            } else {
                                ((ImageButton) toolbox.getChildAt(i)).setImageResource(R.drawable.ic_quad3d);
                            }
                        }

                        paintView.setShapeType(paintView.isOrtho() ? Quad2D.class : Quad3D.class);

                        break;
                    case 3: // CIRCLE
                        paintView.setShapeType(Circle.class);
                        break;
                    case 4: // STROKE WIDTH
                        if (paintView.getBrush().toggleFill()) {
                            ((ImageButton) toolbox.getChildAt(i)).setImageResource(R.drawable.ic_fill);
                        } else {
                            ((ImageButton) toolbox.getChildAt(i)).setImageResource(R.drawable.ic_fill_none);
                        }
                        break;
                }
            }
        }

        switch (v.getId()) {
            case R.id.fillColorPicker: // FILL COLOR PICKER
                FillColorPicker fillColorPicker = new FillColorPicker(this);
                fillColorPicker.show();
                break;

            case R.id.penColorPicker:
                StrokeColorPicker strokeColorPicker = new StrokeColorPicker(this);
                strokeColorPicker.show();
                break;

            case R.id.save:
                if (paintView.isModified()) {
                    saveConfirmation.show();
                }
                break;

            case R.id.fullScreen:
                fullScreen = !fullScreen;
                findViewById(R.id.tools).setVisibility(fullScreen ? View.GONE : View.VISIBLE);
                actionBar.setVisibility(fullScreen ? View.GONE : View.VISIBLE);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (paintView.isModified()) {
            exitConfirmation.show();
        } else {
            super.onBackPressed();
        }
    }

    private class StrokeColorPicker extends AmbilWarnaDialog {

        /**
         * Create an AmbilWarnaDialog.
         *
         * @param context activity context
         */
        StrokeColorPicker(Context context) {
            super(context, paintView.getBrush().getStrokeColor(), true, new OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) { }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    paintView.getBrush().setStrokeColor(color);
                }
            });
        }
    }

    private class FillColorPicker extends AmbilWarnaDialog {

        /**
         * Create an AmbilWarnaDialog.
         *
         * @param context activity context
         */
        FillColorPicker(Context context) {
            super(context, paintView.getBrush().getFillColor(), true, new OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) { }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    paintView.getBrush().setFillColor(color);
                }
            });
        }
    }

}