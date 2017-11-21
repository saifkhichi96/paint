package sfllhkhan95.doodle.models;

import android.graphics.Bitmap;

public class Thumbnail {
    private Bitmap icon;
    private String name;

    public Thumbnail(Bitmap icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}