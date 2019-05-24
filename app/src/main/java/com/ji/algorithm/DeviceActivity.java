package com.ji.algorithm;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ji.utils.LogUtils;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceActivity extends AppCompatActivity {
    private String TAG = "DeviceActivity";

    /**
     * 激活组件的请求码
     */
    private static final int REQUEST_CODE_ACTIVE_COMPONENT = 1;

    /**
     * 设备安全管理服务，2.2之前需要通过反射技术获取
     */
    private DevicePolicyManager mDevicePolicyManager = null;

    /**
     * 对应自定义DeviceAdminReceiver的组件
     */
    private ComponentName mComponentName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, DeviceReceiver.class);

        /**
         * 激活设备管理器
         */
        findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    Toast.makeText(DeviceActivity.this, "设备管理器已激活", Toast.LENGTH_SHORT).show();
                } else {
                    // 打开管理器的激活窗口
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    // 指定需要激活的组件
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "(激活窗口中的描述信息)");
                    startActivityForResult(intent, REQUEST_CODE_ACTIVE_COMPONENT);
                }
            }
        });

        /**
         * 取消激活
         */
        findViewById(R.id.btn_cancel_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    mDevicePolicyManager.removeActiveAdmin(mComponentName);
                    Toast.makeText(DeviceActivity.this, "将触发deviceAdminReceiver.onDisabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeviceActivity.this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 锁屏
         */
        findViewById(R.id.btn_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    mDevicePolicyManager.lockNow();
                } else {
                    Toast.makeText(DeviceActivity.this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 禁止使用摄像头
         */
        findViewById(R.id.btn_camera_disabled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    mDevicePolicyManager.setCameraDisabled(mComponentName, true);
                }
            }
        });

        /**
         * 启动摄像头
         */
        findViewById(R.id.btn_camera_abled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    mDevicePolicyManager.setCameraDisabled(mComponentName, false);
                }
            }
        });

        /**
         * 设置密码
         */
        findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    mDevicePolicyManager.resetPassword("1234", 1);
                }
            }
        });

        /**
         * 取消密码
         */
        findViewById(R.id.btn_cancel_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()) {
                    mDevicePolicyManager.resetPassword("", 0);
                }
            }
        });

        // GoogleCDD
        final TextView displayView = findViewById(R.id.tv_display);
        findViewById(R.id.btn_keyguard_disabled_fingerprint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 21) {
                    mDevicePolicyManager.setKeyguardDisabledFeatures(mComponentName, DevicePolicyManager.KEYGUARD_DISABLE_FINGERPRINT);
                }
            }
        });
        findViewById(R.id.btn_keyguard_disabled_fingerprint).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int disabledFeatures = mDevicePolicyManager.getKeyguardDisabledFeatures(mComponentName);
                displayView.setText("锁屏禁用功能:0b" + Integer.toBinaryString(disabledFeatures));
                return false;
            }
        });

        findViewById(R.id.btn_keyguard_disabled_face).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 28) {
                    mDevicePolicyManager.setKeyguardDisabledFeatures(mComponentName, DevicePolicyManager.KEYGUARD_DISABLE_FACE);
                }
            }
        });
        findViewById(R.id.btn_keyguard_disabled_face).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int disabledFeatures = mDevicePolicyManager.getKeyguardDisabledFeatures(mComponentName);
                displayView.setText("锁屏禁用功能:0b" + Integer.toBinaryString(disabledFeatures));
                return false;
            }
        });

        findViewById(R.id.btn_password_quality).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDevicePolicyManager.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_BIOMETRIC_WEAK);
            }
        });
        findViewById(R.id.btn_password_quality).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int passwordQuality = mDevicePolicyManager.getPasswordQuality(null);
                displayView.setText("密码质量:0x" + Integer.toHexString(passwordQuality));
                return false;
            }
        });
    }

    /**
     * 判断该组建是否有系统管理员的权限（系统安全-设备管理器 中是否激活）
     *
     * @return
     */
    private boolean isAdminActive() {
        return mDevicePolicyManager.isAdminActive(mComponentName);
    }

    /**
     * 用户是否点击激活或取消的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTIVE_COMPONENT) {
            // 激活组件的响应
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "用户取消激活", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "用户触发激活", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
