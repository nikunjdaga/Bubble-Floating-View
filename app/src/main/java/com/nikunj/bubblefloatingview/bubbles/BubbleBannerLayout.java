package com.nikunj.bubblefloatingview.bubbles;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by nikunj on 3/8/16.
 */
public class BubbleBannerLayout extends FloatingViewLayout {
    private boolean isAttachedToWindow = false;

    public BubbleBannerLayout(Context context) {
        super(context);
    }

    public BubbleBannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleBannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false ;
    }

    @Override
    public void setVisibility(int visibility) {
        if (isAttachedToWindow) {
            if (visibility != getVisibility()) {
                if(visibility == VISIBLE) bannerShownAnimation();
                else bannerHiddenAnimation();
            }
        }
        super.setVisibility(visibility);
    }

    private void bannerShownAnimation() {
    }

    private void bannerHiddenAnimation() {
    }
}