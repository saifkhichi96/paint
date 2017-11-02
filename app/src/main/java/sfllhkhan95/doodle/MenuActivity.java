package sfllhkhan95.doodle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sfllhkhan95.doodle.utils.DoodleDatabase;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.whiteDoodle).setOnClickListener(this);
        findViewById(R.id.blackDoodle).setOnClickListener(this);
        findViewById(R.id.fromImage).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] savedDoodles = DoodleDatabase.listDoodles();
        if (savedDoodles != null && savedDoodles.length > 0) {
            LinearLayout doodles = (LinearLayout) findViewById(R.id.savedDoodles);
            doodles.removeAllViews();

            for (final String doodle : savedDoodles) {
                View.inflate(this, R.layout.saved_doodle, doodles);
                View view = doodles.getChildAt(doodles.getChildCount() - 1);

                TextView title = view.findViewById(R.id.doodleTitle);
                title.setText(doodle);

                ImageView thumbnail = view.findViewById(R.id.doodleThumbnail);
                thumbnail.setImageBitmap(DoodleDatabase.loadDoodle(doodle, 100, 100));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("DOODLE", doodle);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        switch (v.getId()) {
            case R.id.whiteDoodle:
                intent.putExtra("BG_COLOR", Color.WHITE);
                startActivity(intent);
                break;

            case R.id.blackDoodle:
                intent.putExtra("BG_COLOR", Color.BLACK);
                startActivity(intent);
                break;

            case R.id.fromImage:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }
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
}
