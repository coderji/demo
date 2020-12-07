package com.ji.terminal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SarTable {
    private static int mModemTable = 0, mModemTableEasy = 0, mWifiTable = 0, mWifiTableEasy = 0;
    private static boolean mRfPort = false, mConnected = false,  mEarpiece = false;
    private static boolean mTopSensorActive = false, mBottomSensorActive= false, mHotspot = false;
    private static boolean mWLAN = false, mWWAN = false;
    private static final int RF = 1 << 0;
    private static final int CONNECTED = 1 << 1;
    private static final int EARPIECE = 1 << 2;
    private static final int TOP = 1 << 3;
    private static final int BOTTOM = 1 << 4;
    private static final int HOTSPOT = 1 << 5;
    private static final int WLAN = 1 << 0;
    private static final int WWAN = 1 << 1;

    private static int findModemTable() {
        int table = 0;
        if (mConnected) {
            if (mRfPort) {
                table = 1;
            } else {
                if (mEarpiece) {
                    table = 2;
                } else {
                    if (mTopSensorActive && mBottomSensorActive) {
                        table = 3;
                    } else {
                        if (mHotspot) {
                            table = 7;
                        } else {
                            if (!mTopSensorActive && mBottomSensorActive) {
                                table = 6;
                            } else {
                                table = 4;
                            }
                        }
                    }
                }
            }
        }
        return table;
    }

    private static int findModemTableEasy() {
        int table = 0;
        if (mRfPort && mConnected) {
            table = 1;
        } else if (!mRfPort && mConnected && mEarpiece) {
            table = 2;
        } else if (!mRfPort && mConnected && !mEarpiece && mTopSensorActive && mBottomSensorActive) {
            table = 3;
        } else if (!mRfPort && mConnected && !mEarpiece && !mBottomSensorActive && !mHotspot) {
            table = 4;
        } else if (!mRfPort && mConnected && !mEarpiece && !mTopSensorActive && mBottomSensorActive && !mHotspot) {
            table = 6;
        } else if (!mRfPort && mConnected && !mEarpiece && !(mTopSensorActive && mBottomSensorActive) && mHotspot) {
            table = 7;
        }
        return table;
    }

    private static void checkModemTable() {
        for (int state = 0; state <= (RF | CONNECTED | EARPIECE | TOP | BOTTOM | HOTSPOT); state++) {
            mRfPort = (state & RF) == RF;
            mConnected = (state & CONNECTED) == CONNECTED;
            mEarpiece = (state & EARPIECE) == EARPIECE;
            mTopSensorActive = (state & TOP) == TOP;
            mBottomSensorActive = (state & BOTTOM) == BOTTOM;
            mHotspot = (state & HOTSPOT) == HOTSPOT;
            mModemTable = findModemTable();
            mModemTableEasy = findModemTableEasy();
            if (mModemTable == mModemTableEasy) {
                System.out.println("state:" + state + " table:" + mModemTable);
            } else {
                System.out.println("state:" + state + " " + dumpModemInfo());
            }
        }
    }

    private static String dumpModemInfo() {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date()));
        debugInfo.append("\nModem\t| RF\t| Connect\t| Earpiece\t| Top\t| Bottom\t| Hotspot\n ");
        debugInfo.append(mModemTable);
        debugInfo.append("/");
        debugInfo.append(mModemTableEasy);
        debugInfo.append("\t| ");
        debugInfo.append(mRfPort ? "Yes" : "No");
        debugInfo.append("\t| ");
        debugInfo.append(mConnected ? "Yes" : "No");
        debugInfo.append("\t\t| ");
        debugInfo.append(mEarpiece ? "Yes" : "No");
        debugInfo.append("\t\t| ");
        debugInfo.append(mTopSensorActive ? "Yes" : "No");
        debugInfo.append("\t| ");
        debugInfo.append(mBottomSensorActive ? "Yes" : "No");
        debugInfo.append("\t\t| ");
        debugInfo.append(mHotspot ? "Yes" : "No");
        debugInfo.append("\n");
        return debugInfo.toString();
    }

    private static int findWifiTable() {
        int table = 0;
        if (mWLAN) {
            if (mEarpiece) {
                table = 4;
            } else {
                if (mTopSensorActive && mBottomSensorActive) {
                    table = 6;
                } else {
                    if (mHotspot) {
                        table = 2;
                    }
                }
            }
        }
        return table;
    }

    private static int findWifiTableEasy() {
        int table = 0;
        if (mWLAN && !mEarpiece && !mTopSensorActive && !mBottomSensorActive && !mHotspot) {
            table = 0;
        } else if (mWLAN && !mEarpiece /*&& !mTopSensorActive && !mBottomSensorActive*/ && mHotspot) {
            table = 2;
        } else if (mWLAN && mEarpiece) {
            table = 4;
        } else if (mWLAN && !mEarpiece && mTopSensorActive && mBottomSensorActive) {
            table = 6;
        }
        return table;
    }

    private static void checkWifiTable() {
        for (int state = 0; state <= (WLAN | WWAN | EARPIECE | TOP | BOTTOM | HOTSPOT); state++) {
            mWLAN = (state & WLAN) == WLAN;
            mWWAN = (state & WWAN) == WWAN;
            mEarpiece = (state & EARPIECE) == EARPIECE;
            mTopSensorActive = (state & TOP) == TOP;
            mBottomSensorActive = (state & BOTTOM) == BOTTOM;
            mHotspot = (state & HOTSPOT) == HOTSPOT;
            mWifiTable = findWifiTable();
            mWifiTableEasy = findWifiTableEasy();
            if (mWifiTable == mWifiTableEasy) {
                System.out.println("state:" + state + " table:" + mWifiTable);
            } else {
                System.out.println("state:" + state + " " + dumpWifiInfo());
            }
        }
    }

    private static String dumpWifiInfo() {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date()));
        debugInfo.append("\nWi-Fi\t| WLAN\t| WWAN\t| Earpiece\t| Top\t| Bottom\t| Hotspot\n ");
        debugInfo.append(mWifiTable);
        debugInfo.append("/");
        debugInfo.append(mWifiTableEasy);
        debugInfo.append("\t| ");
        debugInfo.append(mWLAN ? "Yes" : "No");
        debugInfo.append("\t| ");
        debugInfo.append(mWWAN ? "Yes" : "No");
        debugInfo.append("\t| ");
        debugInfo.append(mEarpiece ? "Yes" : "No");
        debugInfo.append("\t\t| ");
        debugInfo.append(mTopSensorActive ? "Yes" : "No");
        debugInfo.append("\t| ");
        debugInfo.append(mBottomSensorActive ? "Yes" : "No");
        debugInfo.append("\t\t| ");
        debugInfo.append(mHotspot ? "Yes" : "No");
        debugInfo.append("\n");
        return debugInfo.toString();
    }

    public static int main(String[] args) {
        checkWifiTable();
        return 0;
    }
}
