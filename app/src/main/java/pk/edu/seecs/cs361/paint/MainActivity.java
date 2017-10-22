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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import pk.edu.seecs.cs361.paint.shapes.Quad2D;
import pk.edu.seecs.cs361.paint.shapes.Quad3D;
import pk.edu.seecs.cs361.paint.view.PaintView;
import pk.edu.seecs.cs361.paint.shapes.Circle;
import pk.edu.seecs.cs361.paint.shapes.Line;
import pk.edu.seecs.cs361.paint.shapes.Pen;
import pk.edu.seecs.cs361.paint.utils.ActionBarManager;
import pk.edu.seecs.cs361.paint.view.ToolboxView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // A custom OpenGL ES canvas to draw on
    private PaintView paintView;

    // Toolbox contains the drawing tools
    private ToolboxView toolbox;

    // Dialog boxes to confirm certain permanent actions (i.e. revert and save)
    private AlertDialog revertConfirmation;
    private AlertDialog saveConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.actionBar));
        getSupportActionBar().setTitle("");

        // Create confirmation dailogs
        revertConfirmation = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
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
                .setTitle("Save doodle to Galley?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintView.save();
                        revertConfirmation.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        revertConfirmation.dismiss();
                    }
                })
                .create();

        // Add click event listeners to toolbox buttons
        toolbox = (ToolboxView) findViewById(R.id.toolbox);
        toolbox.addUnselectable(4);
        toolbox.addUnselectable(5);
        toolbox.setOnClickListener(this);

        findViewById(R.id.save).setOnClickListener(this);

        // Initialize canvas where everything is drawn

        // Initialize canvas
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        paintView = (PaintView) findViewById(R.id.canvas);
        String savedDoodle = getIntent().getStringExtra("DOODLE");
        if (savedDoodle != null && !savedDoodle.equals("")) {
            paintView.loadFromPath(metrics, savedDoodle);
        }

        paintView = (PaintView) findViewById(R.id.canvas);
        Intent data = getIntent().getParcelableExtra("FROM_GALLERY");
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            paintView.loadFromBitmap(metrics, BitmapFactory.decodeFile(picturePath));
        } else {
            paintView.init(metrics);
            paintView.getCanvas().setColor(getIntent().getIntExtra("BG_COLOR", Color.BLACK));
        }

        // Select Pen tool by default
        toolbox.selectTool(0);

        ImageButton btnPick = (ImageButton) findViewById(R.id.colorPicker);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(false);
            }
        });
    }

    private void openDialog(boolean supportsAlpha) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, paintView.getBrush().getStrokeColor(), supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                paintView.getBrush().setStrokeColor(color);
                paintView.getBrush().setFillColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
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
                    case 5: // COLOR PICKER
                        // TODO: Implement color picker
                        // Display color picker here. And set color using paintCanvas.setBrushColor once a color is picked.
                        break;
                }
            }
        }

        switch (v.getId()) {
            case R.id.save:
                saveConfirmation.show();
                break;
        }

    }

}