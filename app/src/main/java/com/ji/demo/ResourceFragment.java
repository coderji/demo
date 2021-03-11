package com.ji.demo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ji.util.Log;

public class ResourceFragment extends Fragment {
    private static final String TAG = "ResourceFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        int id = context.getResources().getIdentifier("config_dialogCornerRadius", "dimen", "android");
        if (id > 0) {
            float config_dialogCornerRadius = context.getResources().getDimension(id);
            Log.d(TAG, "config_dialogCornerRadius:" + config_dialogCornerRadius);
        } else {
            Log.d(TAG, "getIdentifier fail");
        }
    }
}
