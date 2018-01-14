package sfllhkhan95.doodle.utils;

import android.graphics.Bitmap;

public interface SignInListener {

    void onSignedIn(String userName, Bitmap profilePicture);

    void onSignedOut();

}
