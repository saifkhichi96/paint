package pk.aspirasoft.core.io;

import android.support.annotation.NonNull;


public interface OnCompleteListener<T> {

    void onSuccess(@NonNull T result);

    void onFailure(@NonNull Exception ex);

}
