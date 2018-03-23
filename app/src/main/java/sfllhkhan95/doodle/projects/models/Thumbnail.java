package sfllhkhan95.doodle.projects.models;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.projects.utils.DoodleDatabase;
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater;

public class Thumbnail implements View.OnClickListener {

    private final ThumbnailInflater inflater;

    private Bitmap icon;
    private String name;

    public Thumbnail(ThumbnailInflater inflater, Bitmap icon, String name) {
        this.inflater = inflater;
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

    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle(view.getContext().getResources().getString(R.string.confirmDeleteTitle))
                .setMessage(view.getContext().getResources().getString(R.string.confirmDeleteMessage))
                .setPositiveButton(view.getContext().getResources().getString(R.string.labelYes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DoodleDatabase.removeDoodle(Thumbnail.this.getName());
                        inflater.run();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(view.getContext().getResources().getString(R.string.labelNo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

}