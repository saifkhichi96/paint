package sfllhkhan95.doodle.view;

import android.content.Context;

import yuku.ambilwarna.AmbilWarnaDialog;

public class StrokeColorPicker implements AmbilWarnaDialog.OnAmbilWarnaListener {

    private final AmbilWarnaDialog dialog;
    private final PaintView paintView;

    public StrokeColorPicker(Context context, final PaintView paintView) {
        this.paintView = paintView;
        this.dialog = new AmbilWarnaDialog(context, paintView.getBrush().getStrokeColor(), true, this);
    }

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {

    }

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        this.paintView.getBrush().setStrokeColor(color);
    }

    public void show() {
        this.dialog.show();
    }
}