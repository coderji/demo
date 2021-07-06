package com.ji.demo;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.euicc.EuiccManager;

import androidx.annotation.NonNull;

import com.ji.util.BaseFragment;
import com.ji.util.Log;

import java.util.List;

public class TelephonyFragment extends BaseFragment {
    private static final String TAG = "TelephonyFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getEuiccManager(context);
    }

    private void getEuiccManager(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        EuiccManager euiccManager = (EuiccManager) context.getSystemService(Context.EUICC_SERVICE);
        EuiccManager euiccManager1 = null, euiccManager2 = null;
        try {
            List<UiccCardInfo> list = telephonyManager.getUiccCardsInfo();
            for (UiccCardInfo info : list) {
                Log.d(TAG, "info:" + info);
                if (info.getSlotIndex() == 0) {
                    euiccManager1 = euiccManager.createForCardId(info.getCardId());
                } else if (info.getSlotIndex() == 1) {
                    euiccManager2 = euiccManager.createForCardId(info.getCardId());
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "getEuiccManager" + e);
        }
        Log.d(TAG, "euiccManager1:" + euiccManager1 + " euiccManager2:" + euiccManager2);
    }
}
