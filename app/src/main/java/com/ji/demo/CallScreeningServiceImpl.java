package com.ji.demo;

import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import androidx.annotation.NonNull;
import com.ji.util.Log;

public class CallScreeningServiceImpl extends CallScreeningService {
    public static final String TAG = "CallScreeningServiceImpl";

    @Override
    public void onScreenCall(@NonNull Call.Details details) {
        String tel = details.getHandle().getSchemeSpecificPart();
        Log.d(TAG, "onScreenCall tel:" + tel);
        CallResponse.Builder builder = new CallResponse.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setSilenceCall(true);
        }
        respondToCall(details, builder.build());
    }
}
