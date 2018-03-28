package com.nikunj.bubblefloatingview.bubbles;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;

import com.nikunj.bubblefloatingview.R;


/**
 * Created by nikunj on 26/7/16.
 */
public class BubbleTrashLayout extends FloatingViewLayout {
    private boolean isMagnetic = false;
    private boolean isAttachedToWindow = false;

    public BubbleTrashLayout(Context context) {
        super(context);
    }

    public BubbleTrashLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleTrashLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
                if(visibility == VISIBLE) trashShownAnimation();
                else trashHiddenAnimation();
            }
        }
        super.setVisibility(visibility);
    }

    protected void applyMagnetism() {
        if (!isMagnetic) {
            isMagnetic = true;
            trashApplyMagnetismAnimation();
        }
    }

    protected void vibrate() {
        final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(BubbleConstants.VIBRATION_DURATION_IN_MS);
    }

    protected void releaseMagnetism() {
        if (isMagnetic) {
            isMagnetic = false;
            trashReleaseMagnetismAnimation();
        }
    }

    private void trashShownAnimation(){
        playAnimation(R.animator.trash_shown_animator);
    }

    private void trashHiddenAnimation(){
        playAnimation(R.animator.trash_hide_animator);
    }

    private void trashApplyMagnetismAnimation(){
        playAnimation(R.animator.trash_apply_magnetism_animator);
    }

    private void trashReleaseMagnetismAnimation(){
        playAnimation(R.animator.trash_hide_magnetism_animator);
    }
}
