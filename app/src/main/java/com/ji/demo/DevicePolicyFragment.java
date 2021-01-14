package com.ji.demo;

import android.app.admin.DevicePolicyManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ji.util.Log;

public class DevicePolicyFragment extends Fragment {
    private static final String TAG = "DevicePolicyFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        final DevicePolicyManager dpm =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        Log.d(TAG, "dpm:" + dpm);
        long strongAuthTime = dpm.getRequiredStrongAuthTimeout(null);
        Log.d(TAG, "getRequiredStrongAuthTimeout:" + strongAuthTime);
    }
}
