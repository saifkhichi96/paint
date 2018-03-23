package sfllhkhan95.doodle.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import sfllhkhan95.doodle.MainActivity;
import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.models.Thumbnail;

@UiThread
public class ThumbnailInflater implements Runnable, AdapterView.OnItemClickListener {

    private final Activity activity;
    private String[] savedProjects;

    public ThumbnailInflater(Activity activity) {
        this.activity = activity;
    }

    public void setSavedProjects(String[] savedProjects) {
        this.savedProjects = savedProjects;
    }

    @NonNull
    private List<Thumbnail> getThumbnails() {
        List<Thumbnail> thumbnails = new ArrayList<>();
        Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), android.R.drawable.ic_menu_add);
        thumbnails.add(new Thumbnail(this, icon, "NEW PROJECT"));
        if (savedProjects != null) {
            for (String projectName : savedProjects) {
                Bitmap thumbnailBitmap;
                if ((thumbnailBitmap = DoodleDatabase.loadDoodle(projectName, 100, 100)) != null) {
                    Thumbnail thumbnail = new Thumbnail(this, thumbnailBitmap, projectName);
                    thumbnails.add(thumbnail);
                }
            }
        }
        return thumbnails;
    }

    @Override
    public void run() {
        List<Thumbnail> thumbnails = getThumbnails();
        ThumbnailAdapter adapter = new ThumbnailAdapter(
                activity,
                R.layout.template_thumbnail,
                thumbnails);

        GridView doodlesView = activity.findViewById(R.id.savedDoodles);
        doodlesView.setAdapter(adapter);

        doodlesView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(activity, MainActivity.class);
        if (position == 0) {
            intent.putExtra("BG_COLOR", Color.BLACK);
        } else {
            Thumbnail item = (Thumbnail) parent.getItemAtPosition(position);
            intent.putExtra("DOODLE", item.getName());

            if (!DoodleDatabase.contains(item.getName())) return;
        }
        activity.startActivity(intent);
    }

}