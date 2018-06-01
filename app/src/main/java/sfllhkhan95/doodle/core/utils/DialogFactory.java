package sfllhkhan95.doodle.core.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        return new ConfirmationDialog(context)
                .setHeadline("Revert")
                .setIcon(R.drawable.ic_action_revert)
                .setTitle("Reset to original?")
                .setMessage("This action will erase all unsaved changes. It cannot be reversed. Do you really wish to proceed?")
                .setPositiveButton("Reset", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paintView.clear();
                    }
                }, true)
                .setNegativeButton("Cancel", null, true)
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

    public Dialog saveAsConfirmationDialog(Context context) {
        return new ConfirmationDialog(context)
                .setHeadline("Save")
                .setIcon(R.drawable.ic_action_save_as)
                .setTitle("Save the updated Doodle?")
                .setMessage("Your existing project will be updated in Gallery, and you can continue editing it later.")
                .setPositiveButton("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.save();
                        activity.finish();
                    }
                }, true)
                .setNegativeButton("Save Copy", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.saveAs();
                        activity.finish();
                    }
                }, true)
                .create();
    }

    public Dialog saveConfirmationDialog(Context context) {
        return new ConfirmationDialog(context)
                .setHeadline("Save")
                .setIcon(R.drawable.ic_action_save)
                .setTitle("Save the current Doodle?")
                .setMessage("Your project will be saved to Gallery, and you can continue editing it later.")
                .setPositiveButton("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.save();
                        activity.finish();
                    }
                }, true)
                .setNegativeButton("Cancel", null, true)
                .create();
    }

    public Dialog exitConfirmationDialog(Context context) {
        return new ConfirmationDialog(context)
                .setHeadline("Exit")
                .setIcon(R.drawable.ic_action_info)
                .setTitle("Exit without saving?")
                .setMessage("All unsaved changes would be discarded. Do you really wish to proceed?")
                .setPositiveButton("Discard", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                }, true)
                .setIcon(R.drawable.ic_eraser)
                .setNegativeButton("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paintView.save();
                        activity.finish();
                    }
                }, true)
                .create();
    }

    public static class ConfirmationDialog {

        private final Dialog dialog;

        private String title;
        private String headline;
        private String message;

        @DrawableRes
        private int icon = -1;

        private View.OnClickListener positiveButtonListener;
        private String positiveButtonLabel;
        private boolean dismissAfterPositive = false;

        private View.OnClickListener negativeButtonListener;
        private String negativeButtonLabel;
        private boolean dismissAfterNegative = false;

        public ConfirmationDialog(Context context) {
            this.dialog = new Dialog(context) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.dialog_confirmation);

                    TextView titleView = findViewById(R.id.title);
                    titleView.setText(title);

                    TextView headlineView = findViewById(R.id.headline);
                    headlineView.setText(headline);

                    TextView descriptionView = findViewById(R.id.message);
                    descriptionView.setText(message);

                    if (icon != -1) {
                        ImageView iconView = findViewById(R.id.icon);
                        iconView.setImageResource(icon);
                    }

                    if (!positiveButtonLabel.isEmpty()) {
                        final Button positiveButton = findViewById(R.id.positiveButton);
                        positiveButton.setText(positiveButtonLabel);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (positiveButtonListener != null) {
                                    positiveButtonListener.onClick(positiveButton);
                                }
                                if (dismissAfterPositive) {
                                    dismiss();
                                }
                            }
                        });
                        positiveButton.setVisibility(View.VISIBLE);
                    }

                    if (!negativeButtonLabel.isEmpty()) {
                        final Button negativeButton = findViewById(R.id.negativeButton);
                        negativeButton.setText(negativeButtonLabel);
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (negativeButtonListener != null) {
                                    negativeButtonListener.onClick(negativeButton);
                                }
                                if (dismissAfterNegative) {
                                    dismiss();
                                }
                            }
                        });
                        negativeButton.setVisibility(View.VISIBLE);
                    }

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

        public ConfirmationDialog setTitle(String title) {
            this.title = title;
            return this;
        }

        public ConfirmationDialog setHeadline(String headline) {
            this.headline = headline;
            return this;
        }

        public ConfirmationDialog setMessage(String message) {
            this.message = message;
            return this;
        }

        public ConfirmationDialog setIcon(int icon) {
            this.icon = icon;
            return this;
        }

        public ConfirmationDialog setPositiveButton(String label, View.OnClickListener listener, boolean dismiss) {
            positiveButtonLabel = label;
            positiveButtonListener = listener;
            dismissAfterPositive = dismiss;
            return this;
        }

        public ConfirmationDialog setNegativeButton(String label, View.OnClickListener listener, boolean dismiss) {
            negativeButtonLabel = label;
            negativeButtonListener = listener;
            dismissAfterNegative = dismiss;
            return this;
        }

        public Dialog create() {
            return dialog;
        }
    }

}