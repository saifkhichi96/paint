package sfllhkhan95.doodle.view;

import android.content.Context;

import yuku.ambilwarna.AmbilWarnaDialog;

public class FillColorPicker implements AmbilWarnaDialog.OnAmbilWarnaListener {

    private final AmbilWarnaDialog dialog;
    private final PaintView paintView;

    public FillColorPicker(Context context, final PaintView paintView) {
        this.paintView = paintView;
        this.dialog = new AmbilWarnaDialog(context, paintView.getBrush().getFillColor(), true, this);
    }

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {

    }

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        this.paintView.getBrush().setFillColor(color);
    }

    public void show() {
        this.dialog.show();
    }
}