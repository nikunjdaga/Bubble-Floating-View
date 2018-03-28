package com.nikunj.bubblefloatingview.bubbles;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by nikunj on 6/7/16.
 */
public class FloatingViewLayout extends FrameLayout {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public WindowManager.LayoutParams getViewParams() {
        return params;
    }

    public void setViewParams(WindowManager.LayoutParams params) {
        this.params = params;
    }

    public FloatingViewLayout(Context context) {
        super(context);
    }

    public FloatingViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void playAnimation(int animationResourceId) {
        if (!isInEditMode()) {
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getContext(), animationResourceId);
            animator.setTarget(getChildAt(0));
            animator.start();
        }
    }
}
