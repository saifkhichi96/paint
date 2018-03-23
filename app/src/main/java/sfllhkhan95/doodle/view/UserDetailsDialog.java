package sfllhkhan95.doodle.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.models.User;
import sfllhkhan95.doodle.utils.AuthHandler;
import sfllhkhan95.doodle.utils.OnUpdateListener;


public class UserDetailsDialog extends Dialog implements OnUpdateListener {

    private final User DEFAULT_USER = new User();

    private AuthHandler mAuthHandler;

    private UserView mUserView;
    private TextView mBodyTextView;

    private MessengerShareButton mShareButton;
    private View.OnClickListener mShareClickListener;

    public UserDetailsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_details);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
        }

        // Assign views
        mBodyTextView = findViewById(R.id.body);
        mShareButton = findViewById(R.id.messenger_button);
        mUserView = new UserView(this);

        // Assign click listeners
        mShareButton.setOnClickListener(mShareClickListener);
        findViewById(R.id.dismissButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDetailsDialog.this.dismiss();
            }
        });

        // Configure authentication
        mAuthHandler = new AuthHandler(getContext());
        mAuthHandler.setOnUpdateListener(this);
        if (!mAuthHandler.isSignedIn()) {
            mAuthHandler.signIn();
        }

        // Set up Facebook login button
        LoginButton mLoginButton = findViewById(R.id.login_button);
        mAuthHandler.registerFacebookLoginButton(mLoginButton);

        // Display appropriate view
        onUpdate();
    }

    @Override
    public void onUpdate() {
        if (mAuthHandler.isSignedIn()) {
            mUserView.showUser(mAuthHandler.getCurrentUser());

            setBodyText("Now, you can express yourself in your Messenger conversations with Doodle. Try it out now!");
            showShareButton();
        } else {
            mUserView.showUser(DEFAULT_USER);

            setBodyText("Sign in now to express yourself in your Messenger conversations through Doodle.");
            hideShareButton();
        }
    }

    public void setShareClickListener(View.OnClickListener shareClickListener) {
        this.mShareClickListener = shareClickListener;
    }

    public AuthHandler getAuthHandler() {
        return mAuthHandler;
    }

    private void setBodyText(String bodyText) {
        if (mBodyTextView != null) {
            mBodyTextView.setText(bodyText);
        }
    }

    private void showShareButton() {
        if (mShareButton != null) {
            mShareButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideShareButton() {
        if (mShareButton != null) {
            mShareButton.setVisibility(View.GONE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            mAuthHandler.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException ignored) {
            //
        }
    }

}