package com.ji.algorithm;

import android.content.Context;
import android.os.Bundle;

import com.ji.utils.LogUtils;

import androidx.appcompat.app.AppCompatActivity;

public class FragmentActivity extends AppCompatActivity {
    private static final String TAG = "FragmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentByTag("Fragment") == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new Fragment(), "Fragment").commit();
        }
    }

    public static class Fragment extends androidx.fragment.app.Fragment {
        public Fragment() {
            LogUtils.v(TAG, "new Fragment " + getActivity());
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            LogUtils.v(TAG, "Fragment onAttach " + getActivity());
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LogUtils.v(TAG, "Fragment onCreate " + getActivity());
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            LogUtils.v(TAG, "Fragment onActivityCreated " + getActivity());
        }
    }
}
