package com.ji.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.fragment.app.Fragment;

import com.ji.util.Log;

public class BiometricFragment extends Fragment {
    private static final String TAG = "BiometricFragment";
    private WindowManager mWindowManager;
    private SurfaceView mDarkView, mLightView;
    private WindowManager.LayoutParams mDarkLayoutParams, mLightLayoutParams;
    private String HBM_PATH = "/sys/class/backlight/panel0-backlight/hbm";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_biometric, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setBiometricOld(view.findViewById(R.id.biometric_old));
        setBiometricNew(view.findViewById(R.id.biometric_new));
        setOverlayView(view.findViewById(R.id.biometric_overlay));

        view.setId(R.id.biometric);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick view:" + view);
            }
        });
    }

    private void setBiometricOld(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                .setTitle("Biometric login for my app")
                .setMessage("Log in using your biometric credential")
                .setNegativeButton("Cancel", null)
                .create();

        final FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(view.getContext());
        final FingerprintManagerCompat.AuthenticationCallback authenticationCallback = new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                super.onAuthenticationError(errMsgId, errString);
                Toast.makeText(getContext(),
                        "OldAuthentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                super.onAuthenticationHelp(helpMsgId, helpString);
                Log.d(TAG, "OldAuthentication help: " + helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                alertDialog.dismiss();
                Toast.makeText(getContext(),
                        "OldAuthentication succeeded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "OldAuthentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        };

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CancellationSignal cancellationSignal = new CancellationSignal();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        cancellationSignal.cancel();
                    }
                });
                alertDialog.show();
                fingerprintManagerCompat.authenticate(null, 0, cancellationSignal, authenticationCallback, null);
            }
        });
    }

    private void setBiometricNew(View view) {
        final BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(view.getContext()), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getContext(),
                        "NewAuthentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getContext(),
                        "NewAuthentication succeeded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "NewAuthentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("NewBiometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    private void setOverlayView(View view) {
        mWindowManager = view.getContext().getSystemService(WindowManager.class);
        mDarkLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        mDarkLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mDarkLayoutParams.gravity = Gravity.BOTTOM;
        mDarkLayoutParams.setTitle("DarkLayout");

        mLightLayoutParams = new WindowManager.LayoutParams(
                200,
                200,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mLightLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mLightLayoutParams.gravity = Gravity.BOTTOM;
        mLightLayoutParams.y = 300;
        mLightLayoutParams.setTitle("LightLayout");

        mDarkView = new SurfaceView(view.getContext());
        mDarkView.setId(R.id.dark);
//        mDarkView.setBackgroundColor(Color.BLACK);
        mDarkView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mDarkView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated");
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.argb(0.6f, 0, 0, 0));
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed");
            }
        });
        mDarkView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mDarkView.post(() -> SystemProperties.set("config.write.one", HBM_PATH));
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mDarkView.post(() -> SystemProperties.set("config.write.zero", HBM_PATH));
            }
        });

        mLightView = new SurfaceView(view.getContext());
        mLightView.setId(R.id.light);
        mLightView.setBackgroundColor(Color.WHITE);

        view.setOnClickListener(v -> {
            Log.d(TAG, "onClick view:" + v);
            mDarkView.setAlpha(0.6f);
            try {
                mWindowManager.addView(mDarkView, mDarkLayoutParams);
                mWindowManager.addView(mLightView, mLightLayoutParams);
            } catch (RuntimeException e) {
                Log.e(TAG, "addView", e);
            }

            v.postDelayed(() -> {
                try {
                    mWindowManager.removeView(mLightView);
                    mWindowManager.removeView(mDarkView);
                } catch (RuntimeException e) {
                    Log.e(TAG, "removeView", e);
                }
            }, 5000);
        });
    }
}
