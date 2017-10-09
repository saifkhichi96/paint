package pk.edu.seecs.cs361.paint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.whiteDoodle).setOnClickListener(this);
        findViewById(R.id.blackDoodle).setOnClickListener(this);
        findViewById(R.id.fromImage).setOnClickListener(this);
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
                break;
        }
    }
}
