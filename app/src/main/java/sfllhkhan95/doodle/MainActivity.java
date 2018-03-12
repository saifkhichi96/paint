package sfllhkhan95.doodle;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.crashlytics.android.Crashlytics;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.MessengerUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import sfllhkhan95.doodle.core.PaintCanvas;
import sfllhkhan95.doodle.shapes.Circle;
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
import sfllhkhan95.doodle.view.CanvasColorPicker;
import sfllhkhan95.doodle.view.ColorPicker;
import sfllhkhan95.doodle.view.MessengerShareButton;
import sfllhkhan95.doodle.view.PaintView;
import sfllhkhan95.doodle.view.ToolboxView;

public class MainActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, OnToolSelectedListener,
        OnColorPickedListener {

    private static final int REQUEST_CODE_SHARE = 100;
    private static final int REQUEST_CODE_SHARE_TO_MESSENGER = 200;

    // Brush controller
    private SeekBar brushController;

    // A custom OpenGL ES canvas to draw on
    private PaintView paintView;

    // Toolbox contains the drawing tools
    private ToolboxView toolbox;

    // Dialog boxes to confirm certain permanent actions (i.e. revert and save)
    private DialogFactory dialogFactory;

    // Action bars
    private CustomToolbar toolbar;
    private boolean isMaximized;

    // Are we in a REPLY flow?
    private boolean mReplying;
    private MessengerShareButton messengerShareButton;

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
        paintView = findViewById(R.id.canvas);
        Intent intent = getIntent();
        boolean messengerAction = intent.getBooleanExtra("MESSENGER", false);
        if (Intent.ACTION_PICK.equals(intent.getAction()) || messengerAction) {
            messengerShareButton = findViewById(R.id.messenger_share_button);
            messengerShareButton.setVisibility(View.VISIBLE);
            messengerShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShareClicked();
                }
            });
            if (Intent.ACTION_PICK.equals(intent.getAction())) {
                mReplying = true;
                messengerShareButton.setActionText("Replying to");
                messengerShareButton.setDescriptionText("Messenger Conversation");

                MessengerThreadParams mThreadParams = MessengerUtils.getMessengerThreadParamsForIntent(intent);
                String metadata = mThreadParams.metadata;
                List<String> participantIds = mThreadParams.participants;
                if (participantIds != null && participantIds.size() > 1) {
                    // FIXME: This shows user id. Retrieve user's actual full name and display that
                    String replyingTo = participantIds.get(0);

                    messengerShareButton.setDescriptionText(replyingTo);
                }
            }

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            PaintCanvas canvas;
            canvas = new PaintCanvas(metrics);
            canvas.setColor(getIntent().getIntExtra("BG_COLOR", Color.BLACK));
            paintView.setCanvas(canvas);
        } else {
            initCanvas();
        }

        // Create confirmation dialogs
        dialogFactory = new DialogFactory(this, paintView);

        // Add click event listeners to toolbox buttons
        toolbox = (ToolboxView) findViewById(R.id.toolbox);
        toolbox.updatePenColorPicker(paintView.getBrush().getStrokeColor());

        // Start in windowed mode
        this.isMaximized = true;
        toggleMaximized();

        // Select pen
        findViewById(R.id.pen).callOnClick();
    }

    private void initCanvas() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        PaintCanvas canvas;
        String savedDoodle = getIntent().getStringExtra("DOODLE");
        Intent galleryImage = getIntent().getParcelableExtra("FROM_GALLERY");
        if (savedDoodle != null && !savedDoodle.isEmpty()) {
            canvas = PaintCanvas.loadFromPath(metrics, savedDoodle);
        } else if (galleryImage != null) {
            try {
                Uri selectedImage = galleryImage.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                assert selectedImage != null;
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bitmapFromFile = DoodleFactory.loadFromPath(picturePath, metrics.widthPixels, metrics.heightPixels);
                canvas = PaintCanvas.loadFromBitmap(metrics, bitmapFromFile);
            } catch (AssertionError ex) {
                canvas = new PaintCanvas(metrics);
                canvas.setColor(getIntent().getIntExtra("BG_COLOR", Color.BLACK));
            }
        } else {
            canvas = new PaintCanvas(metrics);
            canvas.setColor(getIntent().getIntExtra("BG_COLOR", Color.BLACK));
        }
        paintView.setCanvas(canvas);
        paintView.setShapeType(Pen.class);  // Select Pen by default
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

            case R.id.canvas:
                CanvasColorPicker canvasColorPicker = new CanvasColorPicker(this, paintView.getCanvas().getColor());
                canvasColorPicker.setOnColorPickedListener(new OnColorPickedListener() {
                    @Override
                    public void onColorPicked(int color) {
                        paintView.getCanvas().setColor(color);
                        MainActivity.this.onColorPicked(~color | 0xFF000000);
                        paintView.invalidate();
                    }
                });
                canvasColorPicker.show();
                break;

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

            case R.id.share:
                Bitmap doodle = paintView.getCanvas().getBitmap();

                // Compress bitmap image
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                doodle.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                // Write compressed data to a temporary file
                String path = Environment.getExternalStorageDirectory() + File.separator + "tmp_doodle.jpg";
                File tempFile = new File(path);
                try {
                    tempFile.createNewFile();
                    FileOutputStream fo = new FileOutputStream(tempFile);
                    fo.write(bytes.toByteArray());

                    // Create a share intent
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".sfllhkhan95.doodle.provider", tempFile);
                    share.putExtra(Intent.EXTRA_STREAM, photoURI);

                    // Start share sequence
                    startActivityForResult(Intent.createChooser(share, "Share Doodle"), REQUEST_CODE_SHARE);
                } catch (Exception e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(getString(R.string.unknownError))
                            .create()
                            .show();
                }
                return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SHARE || requestCode == REQUEST_CODE_SHARE_TO_MESSENGER) {
            String path = Environment.getExternalStorageDirectory() + File.separator + "tmp_doodle.jpg";
            File tempFile = new File(path);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
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
                paintView.setShapeType(sfllhkhan95.doodle.shapes.ColorPicker.class);
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
                ColorPicker strokePicker = new ColorPicker(this, paintView.getBrush().getStrokeColor());
                strokePicker.setOnColorPickedListener(this);
                strokePicker.show();
                break;
            case R.id.fillColorPicker:
                ColorPicker fillPicker = new ColorPicker(this, paintView.getBrush().getFillColor());
                fillPicker.setOnColorPickedListener(new OnColorPickedListener() {
                    @Override
                    public void onColorPicked(int color) {
                        paintView.getBrush().setFillColor(color);
                        toolbox.updateFillColorPicker(color);
                    }
                });
                fillPicker.show();
                break;

            case R.id.eraser:
                paintView.setShapeType(Eraser.class);
                break;
        }

        toolbox.updateFillColorPicker(paintView.getBrush().getFillColor());
        toolbox.updatePenColorPicker(paintView.getBrush().getStrokeColor());
    }

    @Override
    public void onColorPicked(int color) {
        paintView.getBrush().setStrokeColor(color);
        toolbox.updatePenColorPicker(color);
    }

    public void onShareClicked() {
        try {
            // Disallow sharing empty projects
            if (!paintView.isModified()) {
                return;
            }

            // Get the drawn bitmap from paint canvas
            PaintCanvas canvas = paintView.getCanvas();
            Bitmap bitmapToShare = canvas.getBitmap();

            // Compress bitmap
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapToShare.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            // Write to a temporary file
            String path = Environment.getExternalStorageDirectory() + File.separator + "tmp_doodle.jpg";
            File tempFile = new File(path);
            tempFile.createNewFile();

            FileOutputStream fo = new FileOutputStream(tempFile);
            fo.write(bytes.toByteArray());

            // Get a shareable file URI
            String contentType = "image/jpeg";
            Uri contentUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".sfllhkhan95.doodle.provider",
                    tempFile
            );

            // Start share sequence
            if (mReplying) {
                messengerShareButton.sendReply(this, contentType, contentUri);
            } else {
                messengerShareButton.sendMessage(this, contentType, contentUri, REQUEST_CODE_SHARE_TO_MESSENGER);
            }
        } catch (Exception e) { // If, for some reason, sharing fails
            // Log exception for error tracking
            Crashlytics.logException(e);

            // Display a nice error message to the user
            Snackbar.make(
                    messengerShareButton,
                    getString(R.string.unknownError),
                    BaseTransientBottomBar.LENGTH_LONG
            ).show();
        }
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