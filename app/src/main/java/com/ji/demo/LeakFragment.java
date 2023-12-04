package com.ji.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ji.utils.LogUtils;

import java.io.InputStream;

public class LeakFragment extends Fragment {
    private static final String TAG = "LeakFragment";
    private final Handler mLeaksHandler = new LeaksHandler();
    private static final int MSG_REPLACE = 0;
    private static final int MSG_LOG = 1;
    private Bitmap mBitmap;
    private byte[] mByte = new byte[1024 * 1024 * 8];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "0x" + Integer.toHexString(hashCode()) + " onCreateView");
        return inflater.inflate(R.layout.fragment_leak, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputStream inputStream = getResources().openRawResource(R.raw.x);
        mBitmap = BitmapFactory.decodeStream(inputStream);
        ImageView imageView = view.findViewById(R.id.leak_img);
        imageView.setImageBitmap(mBitmap);

        mLeaksHandler.sendEmptyMessageDelayed(MSG_LOG, 60 * 60 * 1000);
        mLeaksHandler.sendEmptyMessageDelayed(MSG_REPLACE, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, "0x" + Integer.toHexString(hashCode()) + " onDestroyView");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtils.d(TAG, "0x" + Integer.toHexString(hashCode()) + " onLowMemory");
    }

    class LeaksHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_REPLACE) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, new LeakFragment())
                        .commit();
            } else if (msg.what == MSG_LOG) {
                LogUtils.d(TAG, "0x" + Integer.toHexString(hashCode()) + " MSG_LOG mBitmap:" + mBitmap + " mByte:0x" + Integer.toHexString(mByte.hashCode()));
            }
        }
    }
}
