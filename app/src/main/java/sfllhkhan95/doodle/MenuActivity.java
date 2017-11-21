package sfllhkhan95.doodle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import sfllhkhan95.doodle.models.Thumbnail;
import sfllhkhan95.doodle.utils.DoodleDatabase;
import sfllhkhan95.doodle.utils.ThumbnailAdapter;

public class MenuActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 200;

    private final ProjectScanner scanner = new ProjectScanner();
    private final ThumbnailInflater inflater = new ThumbnailInflater();
    private String[] savedProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("HOME");
        setSupportActionBar(toolbar);

        findViewById(R.id.menuActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("BG_COLOR", Color.BLACK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fromImage:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("FROM_GALLERY", data);
            startActivity(intent);
        }


    }

    @UiThread
    private class ThumbnailInflater implements Runnable,
            AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        @Nullable
        private List<Thumbnail> getThumbnails() {
            List<Thumbnail> thumbnails = null;
            if (savedProjects != null && savedProjects.length > 0) {
                thumbnails = new ArrayList<>();

                Bitmap icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_add);
                thumbnails.add(new Thumbnail(icon, "NEW PROJECT"));

                for (String projectName : savedProjects) {
                    Bitmap thumbnailBitmap;
                    if ((thumbnailBitmap = DoodleDatabase.loadDoodle(projectName, 100, 100)) != null) {
                        Thumbnail thumbnail = new Thumbnail(thumbnailBitmap, projectName);
                        thumbnails.add(thumbnail);
                    }
                }
            }
            return thumbnails;
        }

        @Override
        public void run() {
            List<Thumbnail> thumbnails = getThumbnails();
            if (thumbnails == null) {
                findViewById(R.id.savedDoodles).setVisibility(View.GONE);
                findViewById(R.id.tapAnywhere).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.savedDoodles).setVisibility(View.VISIBLE);
                findViewById(R.id.tapAnywhere).setVisibility(View.GONE);

                ThumbnailAdapter adapter = new ThumbnailAdapter(
                        MenuActivity.this,
                        R.layout.template_thumbnail,
                        thumbnails);

                GridView doodlesView = (GridView) findViewById(R.id.savedDoodles);
                doodlesView.setAdapter(adapter);

                doodlesView.setOnItemClickListener(this);
                doodlesView.setOnItemLongClickListener(this);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            if (position == 0) {
                intent.putExtra("BG_COLOR", Color.BLACK);
            } else {
                Thumbnail item = (Thumbnail) parent.getItemAtPosition(position);
                intent.putExtra("DOODLE", item.getName());

                if (!DoodleDatabase.contains(item.getName())) return;
            }
            startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
            if (position > 0) {
                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle(getString(R.string.confirmDeleteTitle))
                        .setMessage(getString(R.string.confirmDeleteMessage))
                        .setPositiveButton(getString(R.string.labelYes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Thumbnail item = (Thumbnail) parent.getItemAtPosition(position);
                                DoodleDatabase.removeDoodle(item.getName());
                                run();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.labelNo), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                return true;
            }
            return false;
        }
    }

    private class ProjectScanner extends Thread {

        private Handler mHandler = new Handler();
        private boolean isListening = true;

        @Override
        public void run() {
            savedProjects = DoodleDatabase.listDoodles();
            runOnUiThread(inflater);

            if (isListening) {
                mHandler.postDelayed(this, 100);
            }
        }

        @Override
        public synchronized void start() {
            mHandler.post(this);
        }

        void finish() {
            isListening = false;
        }

    }

}
