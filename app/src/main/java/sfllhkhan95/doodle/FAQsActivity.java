package sfllhkhan95.doodle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import us.feras.mdv.MarkdownView;

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:26 AM
 */
public class FAQsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        MarkdownView markdownView = (MarkdownView) findViewById(R.id.markdownView);
        markdownView.loadMarkdownFile("file:///android_asset/FAQ.md");
    }

}