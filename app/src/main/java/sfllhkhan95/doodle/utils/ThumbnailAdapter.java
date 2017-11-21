package sfllhkhan95.doodle.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.models.Thumbnail;
import sfllhkhan95.doodle.view.ThumbnailView;

public class ThumbnailAdapter extends ArrayAdapter {

    private final Context context;
    private final int gridLayoutId;

    private final List<Thumbnail> thumbnails;

    public ThumbnailAdapter(Context context, int gridLayoutId, List<Thumbnail> thumbnails) {
        super(context, gridLayoutId, thumbnails);
        this.gridLayoutId = gridLayoutId;
        this.context = context;
        this.thumbnails = thumbnails;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View gridItem = convertView;
        ThumbnailView thumbnailView;
        if (gridItem == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            gridItem = inflater.inflate(gridLayoutId, parent, false);

            thumbnailView = new ThumbnailView(
                    (TextView) gridItem.findViewById(R.id.projectTitle),
                    (ImageView) gridItem.findViewById(R.id.projectIcon));

            gridItem.setTag(thumbnailView);
        } else {
            thumbnailView = (ThumbnailView) gridItem.getTag();
        }

        thumbnailView.setThumbnail(thumbnails.get(position));
        return gridItem;
    }

}