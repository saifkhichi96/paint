package sfllhkhan95.doodle.core.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.MainActivity;
import sfllhkhan95.doodle.core.views.PaintView;

public class DialogFactory {

    private final PaintView paintView;
    private final Activity activity;

    public DialogFactory(Activity activity, PaintView paintView) {
        this.activity = activity;
        this.paintView = paintView;
    }

    public Dialog revertConfirmationDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle("Revert to original?")
                .setMessage("This action will erase everything drawn on canvas. It cannot be reversed. Do you really wish to proceed?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintView.clear();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public Dialog shareDialog(final MainActivity context) {
        return new Dialog(context) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.dialog_share);

                // Set click listeners
                findViewById(R.id.messengerShareButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.onShareClicked(true);
                    }
                });
                findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.onShareClicked(false);
                    }
                });

                // Initialize AdMob SDK
                MobileAds.initialize(this.getContext(), "ca-app-pub-6293532072634065~6156179621");

                // Load BANNER Ad
                final AdView mAdView = this.findViewById(R.id.adView);
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mAdView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                    }
                });
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        };
    }


    public Dialog saveConfirmationDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle("Save project to galley?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintView.save();
                        activity.finish();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public Dialog exitConfirmationDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle("Exit without saving?")
                .setMessage("This project has unsaved changes. Do you really wish to proceed?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}