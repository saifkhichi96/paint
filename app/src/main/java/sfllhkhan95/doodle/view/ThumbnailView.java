package sfllhkhan95.doodle.view;

import android.widget.ImageView;
import android.widget.TextView;

import sfllhkhan95.doodle.models.Thumbnail;

public class ThumbnailView {

    private final TextView nameView;
    private final ImageView iconView;

    public ThumbnailView(TextView nameView, ImageView iconView) {
        this.nameView = nameView;
        this.iconView = iconView;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        String name = thumbnail.getName();
        int startFrom = name.indexOf('_');
        int endAt = name.indexOf('.');
        if (startFrom != -1 && endAt != -1) {
            name = name.substring(startFrom + 1, endAt);
        }
        nameView.setText(name);
        iconView.setImageBitmap(thumbnail.getIcon());
    }

}