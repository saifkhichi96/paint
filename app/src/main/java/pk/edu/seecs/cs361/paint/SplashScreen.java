package pk.edu.seecs.cs361.paint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST = 200;

    private final long delay = 1500L;
    private final Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }, delay);

                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            timer.cancel();
        } finally {
            super.onDestroy();
        }
    }
}
