package android.os;

import android.util.Log;

import androidx.annotation.NonNull;

public class SystemProperties {
    private static final String TAG = "SystemProperties";

    public static boolean getBoolean(@NonNull String key, boolean def) {
        Log.d(TAG, "getBoolean key:" + key + " def:" + def);
        return false;
    }
}
