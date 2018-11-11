package sfllhkhan95.doodle.projects;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sfllhkhan95.doodle.DoodleApplication;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.ads.AdManager;
import sfllhkhan95.doodle.auth.utils.LoginController;
import sfllhkhan95.doodle.auth.views.UserView;
import sfllhkhan95.doodle.core.MainActivity;
import sfllhkhan95.doodle.core.SettingsActivity;
import sfllhkhan95.doodle.core.utils.OnUpdateListener;
import sfllhkhan95.doodle.core.views.ConfirmationDialog;
import sfllhkhan95.doodle.projects.utils.DoodleDatabase;
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater;

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 16/06/2018 12:10 AM
 */
public class HomeActivity extends AppCompatActivity implements OnUpdateListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    private static final int REQUEST_TAKE_PHOTO = 100;
    private static final int REQUEST_PICK_PHOTO = 200;

    private LoginController mLoginController;
    private UserView mUserView;

    private ThumbnailInflater thumbnailInflater;

    private RapidFloatingActionHelper rfaHelper;
    private String mCameraPicturePath;

    private AdManager mAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((DoodleApplication) getApplication()).setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        thumbnailInflater = new ThumbnailInflater(this);

        // Build floating actions
        RapidFloatingActionButton composeButton = findViewById(R.id.compose_button);
        RapidFloatingActionLayout composeMenu = findViewById(R.id.compose_list);

        RapidFloatingActionContentLabelList composeList = new RapidFloatingActionContentLabelList(this);
        composeList.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.label_camera))
                .setResId(R.drawable.ic_open_camera)
                .setIconNormalColor(getResources().getColor(R.color.red_900))
                .setIconPressedColor(getResources().getColor(R.color.blue_grey_500a))
                .setLabelColor(getResources().getColor(R.color.red_900))
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.label_gallery))
                .setResId(R.drawable.ic_open_gallery)
                .setIconNormalColor(getResources().getColor(R.color.red_900))
                .setIconPressedColor(getResources().getColor(R.color.blue_grey_500a))
                .setLabelColor(getResources().getColor(R.color.red_900))
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.label_blank))
                .setResId(R.drawable.ic_open_blank)
                .setIconNormalColor(getResources().getColor(R.color.blue_grey_500))
                .setIconPressedColor(getResources().getColor(R.color.blue_grey_500a))
                .setLabelColor(getResources().getColor(R.color.blue_grey_500))
                .setWrapper(2)
        );

        composeList.setItems(items)
                .setIconShadowColor(R.color.blue_grey_500a);

        rfaHelper = new RapidFloatingActionHelper(
                this,
                composeMenu,
                composeButton,
                composeList
        ).build();

        mLoginController = new LoginController(this, ((DoodleApplication) getApplication()).getDialogTheme());
        mLoginController.setOnUpdateListener(this);
        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginController.show();
            }
        });

        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        // Assign views
        mUserView = new UserView(this)
                .setNameView((TextView) findViewById(R.id.nameView))
                .setAvatarView((ImageView) findViewById(R.id.userAvatar));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdManager = AdManager.Companion.getInstance();
        mAdManager.loadVideoAd(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Inflate thumbnails of saved projects
        thumbnailInflater.setSavedProjects(DoodleDatabase.INSTANCE.listDoodles());
        runOnUiThread(thumbnailInflater);

        // Update UI
        onUpdate();
    }

    @Override
    public void onUpdate() {
        // Dismiss login dialog if showing
        if (mLoginController.isShowing()) {
            mLoginController.dismiss();
        }

        // Is a user authenticated?
        boolean authenticated = mLoginController.isSignedIn();

        // Show respective layout
        findViewById(R.id.userView).setVisibility(authenticated ? View.VISIBLE : View.GONE);
        findViewById(R.id.signInButton).setVisibility(authenticated ? View.GONE : View.VISIBLE);
        findViewById(R.id.signOutButton).setVisibility(authenticated ? View.VISIBLE : View.GONE);
        if (authenticated) {
            mUserView.showUser(mLoginController.getCurrentUser());
        }
    }

    @Override
    public void onBackPressed() {
        ConfirmationDialog.Builder mBuilder = new ConfirmationDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setHeadline(getString(R.string.label_exit))
                .setTitle(getString(R.string.confirm_quit))
                .setMessage(getString(R.string.description_quit))
                .setPositiveButton(getString(android.R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HomeActivity.super.onBackPressed();
                    }
                }, true)
                .setNegativeButton(getString(android.R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /* no-op */
                    }
                }, true);

        if (mAdManager != null && mAdManager.isVideoAdLoaded()) {
            mBuilder.setMessage(getString(R.string.description_watch_ad))
                    .setNegativeButton(getString(R.string.label_watch_ad), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAdManager.showVideoAd(HomeActivity.this);
                        }
                    }, false);
        }

        mBuilder.create().show();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoginController != null) {
            mLoginController.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLoginController != null) {
            mLoginController.onActivityResult(requestCode, resultCode, data);
        }

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
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        switch (item.getResId()) {
            case R.drawable.ic_open_blank:
                Intent blankProjectIntent = new Intent(this, MainActivity.class);
                blankProjectIntent.putExtra("BG_COLOR", Color.BLACK);
                startActivity(blankProjectIntent);
                break;
            case R.drawable.ic_open_gallery:
                Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivityForResult(pickPictureIntent, REQUEST_PICK_PHOTO);
                    } catch (ActivityNotFoundException ex) {
                        Snackbar.make(mUserView, "No Gallery application found", Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case R.drawable.ic_open_camera:
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
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        } catch (ActivityNotFoundException ex) {
                            Snackbar.make(mUserView, "No Camera application found",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(mUserView, "Permission to perform storage operations required",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Grant", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ActivityCompat.requestPermissions(HomeActivity.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    100);
                                        }
                                    }).show();
                        }
                    }
                }
                break;
        }
        rfaHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        onRFACItemLabelClick(position, item);
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

    public void signOut(View view) {
        if (mLoginController.getCurrentUser() != null)
            new ConfirmationDialog.Builder(this)
                    .setHeadline(getString(R.string.connected))
                    .setIcon(R.drawable.ic_password)
                    .setTitle("Sign out of Doodle?")
                    .setMessage("You are currently logged in as " + mLoginController.getCurrentUser().getEmail())
                    .setPositiveButton(getString(android.R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mLoginController.signOut();
                        }
                    }, true)
                    .setNegativeButton(getString(android.R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }, true)
                    .create()
                    .show();
    }

}