package com.nikunj.bubblefloatingview.bubbles;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by nikunj on 20/8/16.
 */
public class BubbleFullAdLayout extends FloatingViewLayout {
    private boolean isAttachedToWindow = false;

    public BubbleFullAdLayout(Context context) {
        super(context);
    }

    public BubbleFullAdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleFullAdLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
                if(visibility == VISIBLE) fullAdShownAnimation();
                else fullAdHiddenAnimation();
            }
        }
        super.setVisibility(visibility);
    }

    private void fullAdShownAnimation() {
    }

    private void fullAdHiddenAnimation() {
    }
}
