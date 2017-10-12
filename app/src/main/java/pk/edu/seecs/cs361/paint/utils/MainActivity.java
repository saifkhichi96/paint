package pk.edu.seecs.cs361.paint.utils;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import pk.edu.seecs.cs361.paint.R;
import pk.edu.seecs.cs361.paint.core.PaintCanvas;
import pk.edu.seecs.cs361.paint.utils.ActionBarManager;
import pk.edu.seecs.cs361.paint.view.ToolboxView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // A custom OpenGL ES canvas to draw on
    private PaintCanvas paintCanvas;

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
                        paintCanvas.clear();
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
                        paintCanvas.save();
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

        paintCanvas = (PaintCanvas) findViewById(R.id.canvas);
        String savedDoodle = getIntent().getStringExtra("DOODLE");
        if (savedDoodle != null && !savedDoodle.equals("")) {
            paintCanvas.init(metrics, savedDoodle);
        }

        paintCanvas = (PaintCanvas) findViewById(R.id.canvas);
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

            paintCanvas.init(metrics, BitmapFactory.decodeFile(picturePath));
        } else {
            paintCanvas.init(metrics);
            paintCanvas.setCanvasColor(getIntent().getIntExtra("BG_COLOR", Color.BLACK));
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
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, paintCanvas.getBrushColor(), supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                paintCanvas.setBrushColor(color);
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
        paintCanvas.setCanvasActionListener(actionBarManager);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                paintCanvas.undo();
                return true;

            case R.id.redo:
                paintCanvas.redo();
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
                        paintCanvas.enablePen();
                        break;
                    case 1: // LINE
                        paintCanvas.enableLine();
                        break;
                    case 2: // BOX
                        if (toolbox.getSelectedTool() == 2) {
                            if (paintCanvas.toggle3D()) {
                                ((ImageButton) toolbox.getChildAt(i)).setImageResource(R.drawable.ic_box);
                            } else {
                                ((ImageButton) toolbox.getChildAt(i)).setImageResource(R.drawable.ic_square);
                            }
                        }

                        paintCanvas.enableBox();
                        break;
                    case 3: // CIRCLE
                        paintCanvas.enableCircle();
                        break;
                    case 4: // STROKE WIDTH
                        if (paintCanvas.toggleFilled()) {
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