package sfllhkhan95.doodle;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sfllhkhan95.doodle.ums.view.UserDetailsDialog;
import sfllhkhan95.doodle.utils.DoodleDatabase;
import sfllhkhan95.doodle.utils.ThumbnailInflater;

public class MenuActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 200;

    private final ProjectScanner projectScanner = new ProjectScanner();
    private ThumbnailInflater thumbnailInflater;

    private boolean backPressedOnce = false;

    private UserDetailsDialog mUserDetailsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        thumbnailInflater = new ThumbnailInflater(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserDetailsDialog.show();
            }
        });

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
        mUserDetailsDialog.getAuthHandler().stopTracking();
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
        mUserDetailsDialog.onActivityResult(requestCode, resultCode, data);
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
