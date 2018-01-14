package sfllhkhan95.doodle.utils;

import android.graphics.Bitmap;

import com.facebook.Profile;


public interface ActiveUserTracker {

    void onAvatarReceived(Bitmap avatarBitmap);

    void onProfileReceived(Profile profile);

}