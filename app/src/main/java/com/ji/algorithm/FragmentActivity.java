package com.ji.algorithm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ji.utils.LogUtils;

import androidx.appcompat.app.AppCompatActivity;

public class FragmentActivity extends AppCompatActivity {
    private static final String TAG = "FragmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.v(TAG, "onCreate");
//        setContentView(R.layout.fragment_main);
        if (getSupportFragmentManager().findFragmentByTag("Fragment") == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new Fragment(), "Fragment").commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.v(TAG, "onStart");
    }

    @Override
    public void onAttachFragment(androidx.fragment.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        LogUtils.v(TAG, "onAttachFragment");
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            LogUtils.v(TAG, "Fragment onCreateView " + getActivity());
            return inflater.inflate(R.layout.activity_main, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            LogUtils.v(TAG, "Fragment onViewCreated " + getActivity());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "startActivity SingleInstanceActivity", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), SingleInstanceActivity.class));
                }
            });
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            LogUtils.v(TAG, "Fragment onActivityCreated " + getActivity());
        }

        @Override
        public void onStart() {
            super.onStart();
            LogUtils.v(TAG, "Fragment onStart " + getActivity());
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            LogUtils.v(TAG, "Fragment onDestroyView " + getActivity());
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LogUtils.v(TAG, "Fragment onDestroy " + getActivity());
        }

        @Override
        public void onDetach() {
            super.onDetach();
            LogUtils.v(TAG, "Fragment onDetach " + getActivity());
        }
    }
}
