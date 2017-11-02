package sfllhkhan95.doodle;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import sfllhkhan95.doodle.core.PaintCanvas;
import sfllhkhan95.doodle.shapes.Circle;
import sfllhkhan95.doodle.shapes.ColorPicker;
import sfllhkhan95.doodle.shapes.Eraser;
import sfllhkhan95.doodle.shapes.Line;
import sfllhkhan95.doodle.shapes.Pen;
import sfllhkhan95.doodle.shapes.Quad2D;
import sfllhkhan95.doodle.shapes.Quad3D;
import sfllhkhan95.doodle.utils.ActionBarManager;
import sfllhkhan95.doodle.utils.DialogFactory;
import sfllhkhan95.doodle.utils.DoodleFactory;
import sfllhkhan95.doodle.utils.OnColorPickedListener;
import sfllhkhan95.doodle.utils.OnToolSelectedListener;
import sfllhkhan95.doodle.view.FillColorPicker;
import sfllhkhan95.doodle.view.PaintView;
import sfllhkhan95.doodle.view.StrokeColorPicker;
import sfllhkhan95.doodle.view.ToolboxView;

public class MainActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, OnToolSelectedListener,
        OnColorPickedListener {

    // Brush controller
    SeekBar brushController;

    // A custom OpenGL ES canvas to draw on
    private PaintView paintView;

    // Toolbox contains the drawing tools
    private ToolboxView toolbox;

    // Dialog boxes to confirm certain permanent actions (i.e. revert and save)
    private DialogFactory dialogFactory;

    // Action bars
    private CustomToolbar toolbar;
    private boolean isMaximized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main);

        this.toolbar = new CustomToolbar();

        this.brushController = (SeekBar) findViewById(R.id.brushController);
        this.brushController.setOnSeekBarChangeListener(this);

        // Initialize canvas where everything is drawn
        paintView = (PaintView) findViewById(R.id.canvas);
        initCanvas();

        // Create confirmation dialogs
        dialogFactory = new DialogFactory(this, paintView);

        // Add click event listeners to toolbox buttons
        toolbox = (ToolboxView) findViewById(R.id.toolbox);
        toolbox.setPickerColor(paintView.getBrush().getStrokeColor());

        // Start in windowed mode
        this.isMaximized = true;
        toggleMaximized();
    }

    private void initCanvas() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        PaintCanvas canvas;
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

            Bitmap bitmapFromFile = DoodleFactory.loadFromPath(picturePath, metrics.widthPixels, metrics.heightPixels);
            canvas = PaintCanvas.loadFromBitmap(metrics, bitmapFromFile);
        } else {
            canvas = new PaintCanvas(metrics);
            canvas.setColor(getIntent().getIntExtra("BG_COLOR", Color.TRANSPARENT));
        }
        paintView.setCanvas(canvas);
    }

    private void toggleMaximized() {
        this.isMaximized = !this.isMaximized;
        onMaximizeToggled(this.isMaximized);
    }

    private void onMaximizeToggled(boolean isMaximized) {
        findViewById(R.id.container).setVisibility(isMaximized ? View.GONE : View.VISIBLE);
        findViewById(R.id.tools).setVisibility(isMaximized ? View.GONE : View.VISIBLE);
        toolbar.configure(isMaximized);
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isMaximized) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);

            ActionBarManager mActionBarManager = new ActionBarManager(menu);
            paintView.setCanvasActionListener(mActionBarManager);
            mActionBarManager.sync(paintView);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleMaximized();
                return true;

            case R.id.undo:
                paintView.undo();
                return true;

            case R.id.redo:
                paintView.redo();
                return true;

            case R.id.revert:
                dialogFactory.revertConfirmationDialog(this).show();
                return true;

            case R.id.save:
                if (paintView.isModified()) {
                    dialogFactory.saveConfirmationDialog(this).show();
                }
                return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (paintView.isModified()) {
            dialogFactory.exitConfirmationDialog(this).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onToolSelected(boolean reset, int id) {
        if (reset) {
            paintView.setShapeType(null);
        }
        switch (id) {
            case R.id.pen:
                paintView.setShapeType(Pen.class);
                break;

            case R.id.colorPicker:
                paintView.setShapeType(ColorPicker.class);
                paintView.setOnColorPickedListener(this);
                break;

            case R.id.line:
                paintView.setShapeType(Line.class);
                break;
            case R.id.rect:
                paintView.setShapeType(Quad2D.class);
                break;
            case R.id.box:
                paintView.setShapeType(Quad3D.class);
                break;
            case R.id.circle:
                paintView.setShapeType(Circle.class);
                break;

            case R.id.penColorPicker:
                StrokeColorPicker strokeColorPicker = new StrokeColorPicker(this, paintView);
                strokeColorPicker.show();
                break;
            case R.id.fillColorPicker:
                FillColorPicker fillColorPicker = new FillColorPicker(this, paintView);
                fillColorPicker.show();
                break;

            case R.id.eraser:
                paintView.setShapeType(Eraser.class);
                break;
        }

        toolbox.setPickerColor(paintView.getBrush().getStrokeColor());
    }

    @Override
    public void onColorPicked(int color) {
        paintView.getBrush().setStrokeColor(color);
        toolbox.setPickerColor(color);
    }

    private class CustomToolbar {
        private Toolbar primary;
        private Toolbar secondary;

        public CustomToolbar() {
            primary = (Toolbar) findViewById(R.id.primaryToolbar);
            primary.setNavigationIcon(R.drawable.ic_action_maximize);
            primary.setOverflowIcon(getResources().getDrawable(R.drawable.ic_action_layers));
            primary.setTitle("");

            secondary = (Toolbar) findViewById(R.id.secondaryToolbar);
            secondary.setNavigationIcon(R.drawable.ic_action_minimize);
            secondary.setOverflowIcon(getResources().getDrawable(R.drawable.ic_action_layers));
            secondary.setTitle("");
        }

        public void configure(boolean isMaximized) {
            primary.setVisibility(isMaximized ? View.GONE : View.VISIBLE);
            secondary.setVisibility(isMaximized ? View.VISIBLE : View.GONE);

            setSupportActionBar(isMaximized ? secondary : primary);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}