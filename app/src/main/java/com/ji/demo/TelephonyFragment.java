package com.ji.demo;

import android.content.Context;
import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ji.util.Log;

public class TelephonyFragment  extends Fragment {
    private static final String TAG = "TelephonyFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void listenCallState(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                Log.d(TAG, "onCallStateChanged state:" + state + " phoneNumber:" + phoneNumber);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public static class CallScreeningServiceImpl extends CallScreeningService {
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
}
