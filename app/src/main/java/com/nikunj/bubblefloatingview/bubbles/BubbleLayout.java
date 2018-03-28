package com.nikunj.bubblefloatingview.bubbles;

import android.content.Context;
import android.util.AttributeSet;

import com.nikunj.bubblefloatingview.R;

/**
 * Created by nikunj on 26/7/16.
 */
public class BubbleLayout extends FloatingViewLayout {

    //private final int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    public BubbleLayout(Context context) {
        super(context);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        playStartAnimation();
    }

    protected void playStartAnimation() {
        playAnimation(R.animator.bubble_shown_animator);
    }

    protected void playAnimationClickDown() {
        playAnimation(R.animator.bubble_down_click_animator);
    }

    protected void playAnimationClickUp() {
        playAnimation(R.animator.bubble_up_click_animator);
    }

    protected void move(float deltaX, float deltaY) {
        getViewParams().x += deltaX;
        getViewParams().y += deltaY;
        getWindowManager().updateViewLayout(this, getViewParams());
    }
}
