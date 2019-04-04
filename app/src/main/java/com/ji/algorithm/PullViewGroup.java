package com.ji.algorithm;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ji.utils.LogUtils;

public class PullViewGroup extends FrameLayout {
    private String TAG = "PullViewGroup";
    private View mPullView;
    private int mPullViewHeight = 100;
    private View mScrollView;
    private float mStartY = -1, mScrollUp = -1;

    public PullViewGroup(Context context) {
        this(context, null);
    }

    public PullViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getChildCount() == 1) {
            mScrollView = getChildAt(0);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // mScrollView request disallow intercept touch event
        // super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartY = ev.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                // Negative to check scrolling up, positive to check scrolling down.
                if (!mScrollView.canScrollVertically(-1) && ev.getY() > mStartY) {
                    LogUtils.v(TAG, "onInterceptTouchEvent can't scrolling up");
                    if (mScrollUp == -1) {
                        mScrollUp = ev.getY();

                        mPullView = new View(getContext());
                        ViewGroup.LayoutParams layoutParams =
                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPullViewHeight);
                        mPullView.setLayoutParams(layoutParams);
                        mPullView.setBackgroundColor(Color.GRAY);
                        mPullView.setTranslationY(-mPullViewHeight);
                        addView(mPullView);
                    }
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtils.v(TAG, "onTouchEvent");
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return false;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY() - mScrollUp;
                // Negative to check scrolling up, positive to check scrolling down.
                if (!mScrollView.canScrollVertically(-1) && moveY > 0) {
                    mScrollView.setTranslationY(moveY);
                    mPullView.setTranslationY(-mPullViewHeight + moveY);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                if (mScrollUp != -1) {
                    mScrollUp = -1;

                    mScrollView.setTranslationY(0);
                    removeView(mPullView);
                }
                performClick();
                return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
