package sfllhkhan95.doodle.projects;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import sfllhkhan95.doodle.DoodleApplication;
import sfllhkhan95.doodle.FAQsActivity;
import sfllhkhan95.doodle.PrivacyPolicy;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.auth.UserDetailsDialog;
import sfllhkhan95.doodle.core.MainActivity;
import sfllhkhan95.doodle.projects.utils.DoodleDatabase;
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater;

/**
 * @author saifkhichi96
 * @version 1.0
 *          created on 23/10/2017 2:27 AM
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PHOTO = 100;
    private static final int REQUEST_PICK_PHOTO = 200;

    private final ProjectScanner projectScanner = new ProjectScanner();
    private ThumbnailInflater thumbnailInflater;
    private boolean backPressedOnce = false;
    private UserDetailsDialog mUserDetailsDialog;

    private String mCameraPicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        thumbnailInflater = new ThumbnailInflater(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Register event handlers
        findViewById(R.id.blankProject).setOnClickListener(this);
        findViewById(R.id.fromGallery).setOnClickListener(this);
        findViewById(R.id.fromCamera).setOnClickListener(this);
        findViewById(R.id.profileButton).setOnClickListener(this);

        mUserDetailsDialog = new UserDetailsDialog(this, R.style.DialogTheme);
        mUserDetailsDialog.setShareClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("MESSENGER", true);
                intent.putExtra("BG_COLOR", Color.BLACK);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectScanner.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        projectScanner.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUserDetailsDialog.getAuthHandler() != null) {
            mUserDetailsDialog.getAuthHandler().stopTracking();
        }
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
            case R.id.privacy_policy:
                startActivity(new Intent(getApplicationContext(), PrivacyPolicy.class));
                break;

            case R.id.faqs:
                startActivity(new Intent(getApplicationContext(), FAQsActivity.class));
                break;

            case R.id.about_developer:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.developer_page_url)));
                startActivity(browserIntent);
                break;
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.blankProject:
                Intent blankProjectIntent = new Intent(this, MainActivity.class);
                blankProjectIntent.putExtra("BG_COLOR", Color.BLACK);
                startActivity(blankProjectIntent);
                break;

            case R.id.fromGallery:
                Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pickPictureIntent, REQUEST_PICK_PHOTO);
                }
                break;

            case R.id.fromCamera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile;
                    try {
                        photoFile = createImageFile();
                        Uri photoURI = FileProvider.getUriForFile(this,
                                getApplicationContext().getPackageName() + ".sfllhkhan95.doodle.provider",
                                photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    } catch (Exception ex) {
                        // Error occurred while creating the File
                    }
                }
                break;

            case R.id.profileButton:
                mUserDetailsDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUserDetailsDialog.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_PICK_PHOTO:
                if (data != null) {
                    Intent openGalleryImage = new Intent(getApplicationContext(), MainActivity.class);
                    openGalleryImage.putExtra("FROM_GALLERY", data);
                    startActivity(openGalleryImage);
                }
                break;
            case REQUEST_TAKE_PHOTO:
                if (mCameraPicturePath != null && !mCameraPicturePath.isEmpty()) {
                    Intent openCameraImage = new Intent(getApplicationContext(), MainActivity.class);
                    openCameraImage.putExtra("FROM_CAMERA", mCameraPicturePath);
                    startActivity(openCameraImage);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.backPressedOnce = true;
        Toast.makeText(this, "Press back twice to exit!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 1000);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String path = storageDir + File.separator + "CAMERA_IMAGE.jpg";
        File image = new File(path);
        boolean created = image.createNewFile();
        Log.i(DoodleApplication.TAG, created ? "New temporary file created." : "Temporary file already exists. Overwriting!");

        // Save a file: path for use with ACTION_VIEW intents
        mCameraPicturePath = image.getAbsolutePath();
        return image;
    }

    private class ProjectScanner extends Thread {

        private Handler mHandler = new Handler();
        private boolean isListening = true;

        @Override
        public void run() {
            thumbnailInflater.setSavedProjects(DoodleDatabase.listDoodles());
            runOnUiThread(thumbnailInflater);

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
