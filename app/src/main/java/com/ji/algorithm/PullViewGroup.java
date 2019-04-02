package com.ji.algorithm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ji.utils.LogUtils;

public class PullViewGroup extends LinearLayout {
    private String TAG = "PullViewGroup";
    private View mScrollView;
    private float mDownY;

    public PullViewGroup(Context context) {
        this(context, null);
    }

    public PullViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PullViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Button button = new Button(getContext());
        addView(button, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrollView = getChildAt(1);
                mDownY = ev.getY();
                LogUtils.v(TAG, "onInterceptTouchEvent ACTION_DOWN mScrollView:" + mScrollView);
                // Negative to check scrolling up, positive to check scrolling down.
                if (mScrollView.canScrollVertically(-1)) {
                    LogUtils.v(TAG, "onInterceptTouchEvent ACTION_DOWN canScrollVertically scrolling up");
                } else {
                    LogUtils.v(TAG, "onInterceptTouchEvent ACTION_DOWN canScrollVertically can't scrolling up");
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                mScrollView = getChildAt(1);
                float moveY = ev.getY() - mDownY;
                LogUtils.v(TAG, "onInterceptTouchEvent ACTION_MOVE mScrollView:" + mScrollView + " moveY:" + moveY);
                // Negative to check scrolling up, positive to check scrolling down.
                if (mScrollView.canScrollVertically(-1)) {
                    LogUtils.v(TAG, "onInterceptTouchEvent ACTION_MOVE canScrollVertically scrolling up");
                } else {
                    LogUtils.v(TAG, "onInterceptTouchEvent ACTION_MOVE canScrollVertically can't scrolling up");
                    mScrollView.scrollTo(0, (int) moveY);
                    //return true;
                }
            case MotionEvent.ACTION_UP:
                return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.v(TAG, "onTouchEvent ACTION_DOWN mScrollView:" + mScrollView);
                return false;
            case MotionEvent.ACTION_MOVE:
                LogUtils.v(TAG, "onTouchEvent ACTION_MOVE mScrollView:" + mScrollView);
                return false;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return super.onTouchEvent(event);
    }
}
