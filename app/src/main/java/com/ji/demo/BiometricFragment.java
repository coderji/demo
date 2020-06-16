package com.ji.demo;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.fragment.app.Fragment;

import com.ji.utils.LogUtils;

public class BiometricFragment extends Fragment implements MainActivity.BackPressed {
    private static final String TAG = "BiometricFragment";
    private WindowManager mWindowManager;
    private View mLightView, mDarkView;
    private WindowManager.LayoutParams mLightLayoutParams, mDarkLayoutParams;
    private boolean mLight = false;
    private int mBrightnessDefault, mBrightnessMin, mBrightnessMax, mLastBrightness, mLastMode;
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
        setOverlayView(view.findViewById(R.id.overlay));

        view.setId(R.id.biometric);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.d(TAG, "onClick view:" + view);
            }
        });

        mBrightnessDefault = view.getResources().getInteger(view.getResources().
                getIdentifier("config_screenBrightnessSettingDefault", "integer", "android"));
        mBrightnessMin = view.getResources().getInteger(view.getResources().
                getIdentifier("config_screenBrightnessSettingMinimum", "integer", "android"));
        mBrightnessMax = view.getResources().getInteger(view.getResources().
                getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android"));
        LogUtils.d(TAG, "mBrightnessDefault:" + mBrightnessDefault + " mBrightnessMin:" + mBrightnessMin + " mBrightnessMax:" + mBrightnessMax);
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
                LogUtils.d(TAG, "OldAuthentication help: " + helpString);
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

        mDarkView = new View(view.getContext());
        mDarkView.setId(R.id.dark);
        mDarkView.setBackgroundColor(Color.BLACK);

        mLightView = new FrameLayout(view.getContext());
        mLightView.setId(R.id.light);
        mLightView.setBackgroundColor(Color.WHITE);
        mLightView.setOnClickListener(mRemoveViewListener);

        view.setOnClickListener(mAddViewListener);
    }

    private final View.OnClickListener mAddViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogUtils.d(TAG, "onClick view:" + view);
            if (!mLight) {
                try {
                    if (getContext() != null) {
                        ContentResolver contentResolver = getContext().getContentResolver();
                        mLastBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, mBrightnessDefault);
                        mLastMode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        LogUtils.d(TAG, "brightness " + (mLastMode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL ? "manual" : "automatic") + " " + mLastBrightness + " -> " + mBrightnessMax);
                        if (mLastMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        }
                        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, mBrightnessMax);
                    }
                    mDarkView.setAlpha(calculateOverlayAlpha());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SystemProperties.set("config.write.one", HBM_PATH);
                        }
                    }, 20);

                    mWindowManager.addView(mDarkView, mDarkLayoutParams);
                    mWindowManager.addView(mLightView, mLightLayoutParams);
                    mLight = true;
                } catch (RuntimeException e) {
                    LogUtils.e(TAG, "addView", e);
                }
            }
        }
    };

    private final View.OnClickListener mRemoveViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogUtils.d(TAG, "onClick view:" + view);
            if (mLight) {
                try {
                    SystemProperties.set("config.write.zero", HBM_PATH);
                    if (getContext() != null) {
                        ContentResolver contentResolver = getContext().getContentResolver();
                        int brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, mBrightnessDefault);
                        if (brightness == mBrightnessMax) {
                            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, mLastBrightness);
                        } else {
                            LogUtils.e(TAG, "brightness change");
                        }
                        if (mLastMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                        }
                    }
                    mDarkView.setAlpha(0f);

                    mWindowManager.removeView(mLightView);
                    mWindowManager.removeView(mDarkView);
                    mLight = false;
                } catch (RuntimeException e) {
                    LogUtils.e(TAG, "removeView", e);
                }
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        if (mLight) {
            mRemoveViewListener.onClick(null);
            return true;
        } else {
            return false;
        }
    }

    private float calculateOverlayAlpha() {
        float alpha = 0.3f;
        if (mLastBrightness >= mBrightnessMin && mLastBrightness <= mBrightnessMax) {
            alpha = 0.3f + 0.35f * (mBrightnessMax - mLastBrightness) / (mBrightnessMax - mBrightnessMin);
        }
        LogUtils.d(TAG, "calculateOverlayAlpha mLastBrightness:" + mLastBrightness + " alpha:" + alpha);
        return alpha;
    }
}
