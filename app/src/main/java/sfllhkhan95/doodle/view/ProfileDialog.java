package sfllhkhan95.doodle.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.utils.ActiveUserTracker;
import sfllhkhan95.doodle.utils.SignInListener;

public class ProfileDialog extends Dialog implements ActiveUserTracker,
        OnCompleteListener<AuthResult>, FacebookCallback<LoginResult> {

    private final CallbackManager callbackManager;
    private final FacebookProfileTracker profileTracker;
    private final FirebaseAuth firebaseAuth;

    private FirebaseUser currentUser;
    private Profile currentProfile;

    private View.OnClickListener mShareClickListener;
    private SignInListener signInListener;

    private MessengerShareButton mShareButton;
    private TextView mHeadlineView;
    private TextView mBodyTextView;

    public ProfileDialog(@NonNull Context context) {
        super(context);
        // Configure Facebook authentication parameters
        callbackManager = CallbackManager.Factory.create();
        profileTracker = new FacebookProfileTracker();
        profileTracker.startTracking();

        // If user not authenticated with their Firebase account, sign them in anonymously
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            firebaseAuth.signInAnonymously().addOnCompleteListener(this);
        } else {
            finishAuthConfiguration();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_profile);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
        }

        // Set up Facebook login button
        LoginButton mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("email");
        mLoginButton.registerCallback(callbackManager, this);

        // Assign views
        mHeadlineView = findViewById(R.id.headline);
        mBodyTextView = findViewById(R.id.body);
        mShareButton = findViewById(R.id.messenger_button);

        // Assign click listeners
        mShareButton.setOnClickListener(mShareClickListener);

        // Display appropriate view
        updateUI();
    }

    public void setShareClickListener(View.OnClickListener shareClickListener) {
        this.mShareClickListener = shareClickListener;
    }

    private void setHeadline(String headline) {
        if (mHeadlineView != null) {
            mHeadlineView.setText(headline);
        }
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

    private void updateUI() {
        if (isSignedIn()) {
            setHeadline("Hi, " + currentProfile.getFirstName() + "!");
            setBodyText("Now, you can express yourself in your Messenger conversations with Doodle. Try it out now!");
            showShareButton();

            if (signInListener != null) {
                signInListener.onSignedIn(currentProfile.getFirstName(), null);
            }
        } else {
            setHeadline("You are not connected!");
            setBodyText("Sign in now to express yourself in your Messenger conversations through Doodle.");
            hideShareButton();

            if (signInListener != null) {
                signInListener.onSignedOut();
            }
        }
    }

    /**
     * This callback is triggered when anonymous Firebase authentication is complete
     */
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            currentUser = firebaseAuth.getCurrentUser();
            finishAuthConfiguration();
        }
    }

    /**
     * This callback is triggered when Facebook authentication is successful
     */
    @Override
    public void onSuccess(LoginResult loginResult) {
        Toast.makeText(getContext(), "Sign in successful!", Toast.LENGTH_SHORT).show();
        currentProfile = Profile.getCurrentProfile();
        updateUI();

        linkFacebookAndFirebase(loginResult.getAccessToken());
    }

    private void finishAuthConfiguration() {
        // Is the user already signed in?
        currentProfile = Profile.getCurrentProfile();

        // If they are signed in
        if (isSignedIn()) {
            // Request user avatar (async)
            requestUserAvatar();

            // Display signed in user's details
            updateUI();
        }
    }

    /**
     * Links user's Facebook and Firebase accounts.
     */
    private void linkFacebookAndFirebase(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        // If Firebase account is already authenticated, link Facebook credentials with
        // the same user account
        if (currentUser != null) {
            currentUser.linkWithCredential(credential).addOnCompleteListener(this);
        }

        // If no Firebase user is authenticated, sign up using Facebook credentials
        else {
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this);
        }
    }

    private void requestUserAvatar() {
        if (!isSignedIn()) return;

        AvatarLoader avatarLoader = new AvatarLoader();
        avatarLoader.setActiveUserTracker(this);
        avatarLoader.execute(currentProfile.getId());
    }

    public void onAvatarReceived(Bitmap avatarBitmap) {
        if (isSignedIn() && signInListener != null) {
            signInListener.onSignedIn(currentProfile.getFirstName(), avatarBitmap);
        }
    }

    @Override
    public void onProfileReceived(Profile profile) {
        currentProfile = profile;
        updateUI();
        if (isSignedIn()) {
            requestUserAvatar();
        }
    }

    public void stopTracking() {
        profileTracker.stopTracking();
    }

    private boolean isSignedIn() {
        return currentProfile != null;
    }

    @Override
    public void onCancel() {
        Toast.makeText(getContext(), "Sign in cancelled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(getContext(), "Sign in failed!", Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setSignInListener(SignInListener signInListener) {
        this.signInListener = signInListener;
        updateUI();
    }

    private static class AvatarLoader extends AsyncTask<String, Void, Bitmap> {

        private ActiveUserTracker activeUserTracker;

        void setActiveUserTracker(ActiveUserTracker activeUserTracker) {
            this.activeUserTracker = activeUserTracker;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL imageURL = new URL("https://graph.facebook.com/" + strings[0] + "/picture?width=50&height=50");
                InputStream inputStream = (InputStream) imageURL.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && activeUserTracker != null) {
                activeUserTracker.onAvatarReceived(bitmap);
            }
        }
    }

    private class FacebookProfileTracker extends ProfileTracker {
        @Override
        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            onProfileReceived(currentProfile);
        }
    }

}