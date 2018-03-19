package sfllhkhan95.doodle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sfllhkhan95.doodle.utils.DoodleDatabase;
import sfllhkhan95.doodle.utils.SignInListener;
import sfllhkhan95.doodle.utils.ThumbnailInflater;
import sfllhkhan95.doodle.view.ProfileDialog;

public class MenuActivity extends AppCompatActivity implements SignInListener {

    private static final int RESULT_LOAD_IMAGE = 200;

    private final ProjectScanner scanner = new ProjectScanner();
    private ThumbnailInflater inflater;

    private boolean backPressedOnce = false;

    private ProfileDialog mProfileDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        inflater = new ThumbnailInflater(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.menuActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("BG_COLOR", Color.BLACK);
                startActivity(intent);
            }
        });

        onSignedOut();

        mProfileDialog = new ProfileDialog(this);
        mProfileDialog.setSignInListener(this);
        mProfileDialog.setShareClickListener(new View.OnClickListener() {
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
    public void onSignedIn(String userName, @Nullable Bitmap profilePicture) {
        setTitle(userName);
        if (profilePicture != null) {
            toolbar.setNavigationIcon(new BitmapDrawable(getResources(), profilePicture));
        }
    }

    @Override
    public void onSignedOut() {
        setTitle("Not signed in");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_avatar_placeholder));
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
    public void onDestroy() {
        super.onDestroy();
        mProfileDialog.stopTracking();
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
            case android.R.id.home:
                mProfileDialog.show();
                break;

            case R.id.fromImage:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;

            case R.id.privacy_policy:
                startActivity(new Intent(getApplicationContext(), PrivacyPolicy.class));
                break;

            case R.id.about_developer:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.developer_page_url)));
                startActivity(browserIntent);
                break;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProfileDialog.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("FROM_GALLERY", data);
            startActivity(intent);
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

    private class ProjectScanner extends Thread {

        private Handler mHandler = new Handler();
        private boolean isListening = true;

        @Override
        public void run() {
            inflater.setSavedProjects(DoodleDatabase.listDoodles());
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
