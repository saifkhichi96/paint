package pk.edu.seecs.cs361.paint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        findViewById(R.id.pencil).setOnClickListener(this);
        findViewById(R.id.line).setOnClickListener(this);
        findViewById(R.id.circle).setOnClickListener(this);
        findViewById(R.id.box).setOnClickListener(this);
        findViewById(R.id.colorBucket).setOnClickListener(this);
        findViewById(R.id.colorPicker).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.clear:
                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pencil:
                selectTool(1);
                break;

            case R.id.line:
                selectTool(2);
                break;

            case R.id.circle:
                selectTool(3);
                break;

            case R.id.box:
                selectTool(4);
                break;

            case R.id.colorBucket:
                selectTool(5);
                break;

            case R.id.colorPicker:
                selectTool(6);
                break;

        }
    }

    private void selectTool(int toolId) {
        // Disable all containers
        paintView.setEnabled(false);

        // Select appropriate container
        if (toolId == 1) {
            paintView.setEnabled(true);
        }

    }
}
