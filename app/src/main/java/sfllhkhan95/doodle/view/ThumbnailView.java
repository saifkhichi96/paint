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
        nameView.setText(thumbnail.getName());
        iconView.setImageBitmap(thumbnail.getIcon());
    }

}