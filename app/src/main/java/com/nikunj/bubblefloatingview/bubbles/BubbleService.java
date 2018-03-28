package com.nikunj.bubblefloatingview.bubbles;

import android.app.ActionBar;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nikunj.bubblefloatingview.R;
import com.nikunj.bubblefloatingview.receiver.LockScreenReceiver;

public class BubbleService extends Service {
    private WindowManager windowManager;
    private LayoutInflater inflater;
    private BubbleTrashLayout trashLayout;
    private BubbleLayout bubbleLayout;
    private int chatHeadLayoutWidth;
    private MoveAnimator animator;
    private ImageView bubbleImage;
    private Point sizeWindow;
    private float initialTouchX;
    private float initialTouchY;
    private int initialX;
    private int initialY;
    private long lastTouchDown;
    private VelocityTracker velocityTracker = null;
    private float xVelocity;
    private BubbleBannerLayout bannerLayout;
    private BubbleFullAdLayout fullAdLayout;
    private BroadcastReceiver mReceiver;
    private ImageView fullAdImage;
    private ImageView bannerImage;
    private ImageView trashImage;
    private Context mContext;

    public BubbleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == Service.START_STICKY) {
            handleStart();
            return super.onStartCommand(intent, flags, startId);
        } else {
            return Service.START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeViews();
        removeReceiver();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            goToWall();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            goToWall();
        }
    }

    private void removeViews() {
        if (bubbleLayout != null) windowManager.removeView(bubbleLayout);
        if (trashLayout != null) windowManager.removeView(trashLayout);
        if (bannerLayout != null) windowManager.removeView(bannerLayout);
        if (fullAdLayout != null) windowManager.removeView(fullAdLayout);
    }

    private void handleStart() {
        mContext = this;
        getWindowManager();
        getInflater();
        initReceiver();
        addBubble();
        addTrash();
        addBanner();
        addFullAd();
        hideTrash();
        getWindowSize();
        initializeAnimator();
        chatHeadLayoutTouchEvent();
        setAsForeground();
        hideBubble();
        delayedShowBubble();
    }

    private WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        return windowManager;
    }

    private LayoutInflater getInflater() {
        if (inflater == null) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return inflater;
    }

    private void addTrash() {
        trashLayout = new BubbleTrashLayout(this);
        trashLayout.setWindowManager(getWindowManager());
        trashLayout.setViewParams(getTrashLayoutParams());
        inflater.inflate(R.layout.bubble_trash_layout, trashLayout, true);
        trashImage = (ImageView) trashLayout.findViewById(R.id.trash_image);
/*        Glide.with(this)
                .load(R.drawable.bubble_trash_background)
                .into(trashImage);*/
        addViewToWindow(trashLayout);
    }

    private void addBubble() {
        bubbleLayout = new BubbleLayout(this);
        bubbleLayout.setWindowManager(getWindowManager());
        bubbleLayout.setViewParams(getBubbleLayoutParams(0, 0));
        inflater.inflate(R.layout.bubble_layout, bubbleLayout, true);
        bubbleImage = (ImageView) bubbleLayout.findViewById(R.id.bubble_image);
        Glide.with(this)
                .load(R.drawable.profile)
                .fitCenter()
                .into(bubbleImage);
        addViewToWindow(bubbleLayout);
    }

    private void addBanner() {
        bannerLayout = new BubbleBannerLayout(this);
        bannerLayout.setWindowManager(getWindowManager());
        bannerLayout.setViewParams(getBannerLayoutParams());
        inflater.inflate(R.layout.bubble_banner_layout, bannerLayout, true);
        bannerImage = (ImageView) bannerLayout.findViewById(R.id.banner_image);
        /*Glide.with(this)
                .load(R.drawable.banner300x50)
                .centerCrop()
                .crossFade()
                .into(bannerImage);*/
        addViewToWindow(bannerLayout);
    }

    private void addFullAd() {
        fullAdLayout = new BubbleFullAdLayout(this);
        fullAdLayout.setWindowManager(getWindowManager());
        fullAdLayout.setViewParams(getFullAdLayoutParams());
        inflater.inflate(R.layout.bubble_full_ad_layout, fullAdLayout, true);
        fullAdImage = (ImageView) fullAdLayout.findViewById(R.id.full_ad_img);
        Glide.with(this)
                .load(R.drawable.full_page_image)
                .fitCenter()
                .crossFade()
                .into(fullAdImage);
        addViewToWindow(fullAdLayout);
    }

    private WindowManager.LayoutParams getFullAdLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        return params;
    }


    private WindowManager.LayoutParams getBannerLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        bannerLayout.setVisibility(View.GONE);
        return params;
    }

    private void hideBanner() {
        bannerLayout.setVisibility(View.GONE);
    }

    private void hideTrash() {
        trashLayout.setVisibility(View.GONE);
    }

    private void hideBubble() {
        bubbleLayout.setVisibility(View.GONE);
    }

    private void showBubble() {
        bubbleLayout.setVisibility(View.VISIBLE);
    }

    private void hideFullAd() {
        fullAdLayout.setVisibility(View.GONE);
    }

    private void showFullAd() {
        fullAdLayout.setVisibility(View.VISIBLE);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        mReceiver = new LockScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void removeReceiver() {
        unregisterReceiver(mReceiver);
    }

    /**
     * 3000 ms delay. Needs more testing.
     */
    private void delayedShowBubble() {
        fullAdLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFullAd();
                showBubble();
            }
        }, 3000);
    }

    /*private void updateBannerParams() {
        bannerLayout.measure(0, 0);
        WindowManager.LayoutParams bubbleParams = bubbleLayout.getViewParams();
        WindowManager.LayoutParams bannerParams = bannerLayout.getViewParams();
        if (bannerLayout.getLayoutParams() == null) return;
        bannerParams.height = ActionBar.LayoutParams.WRAP_CONTENT;
        bannerParams.width = ActionBar.LayoutParams.WRAP_CONTENT;
        final int gap = 10;
        if (isLeft) {
            bannerParams.x = bubbleParams.x + gap + bubbleImage.getWidth();
            bannerParams.y = bubbleParams.y;
            bannerLayout.setBackgroundResource(R.drawable.banner_left_bg);
            bannerParams.gravity = Gravity.TOP | Gravity.LEFT;
        } else {
            bannerParams.x = bubbleParams.x - gap - bubbleImage.getWidth() - (chatHeadLayoutWidth / 2);
            bannerParams.y = bubbleParams.y;
            bannerLayout.setBackgroundResource(R.drawable.banner_right_bg);
            bannerParams.gravity = Gravity.TOP | Gravity.RIGHT;
        }
        bannerLayout.setVisibility(View.VISIBLE);
        windowManager.updateViewLayout(bannerLayout, bannerParams);
    }*/

    /**
     * @param x can be random
     * @param y can be random
     */
    private WindowManager.LayoutParams getBubbleLayoutParams(int x, int y) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = x;
        params.y = y;
        return params;
    }

    /**
     * x & y are 0 because it is set in layout files at the bottom
     */
    private WindowManager.LayoutParams getTrashLayoutParams() {
        int x = 0;
        int y = 0;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        params.x = x;
        params.y = y;
        return params;
    }

    private void addViewToWindow(FloatingViewLayout view) {
        windowManager.addView(view, view.getViewParams());
    }

    private void updateSize() {
        chatHeadLayoutWidth = (sizeWindow.x - bubbleLayout.getWidth());
    }

    private void getWindowSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        Display display = getWindowManager().getDefaultDisplay();
        sizeWindow = new Point();
        display.getSize(sizeWindow);
    }

    private void goToWall() {
        if (bubbleLayout.getViewParams().x >= chatHeadLayoutWidth / 2) goToRightWall();
        else goToLeftWall();
    }

    private void goToWall(MotionEvent event) {
        if (initialX >= chatHeadLayoutWidth / 2) {
            if (bubbleLayout.getViewParams().x >= chatHeadLayoutWidth / 2) {
                if (event.getRawX() - initialX >= 0) goToRightWall();
                else {
                    if (Math.abs(xVelocity) >= BubbleConstants.THRESHOLD_X_VELOCITY)
                        goToLeftWall();
                    else goToRightWall();
                }
            } else goToLeftWall();
        } else {
            if (bubbleLayout.getViewParams().x <= chatHeadLayoutWidth / 2) {
                if (event.getRawX() - initialX <= 0) goToLeftWall();
                else {
                    if (xVelocity <= BubbleConstants.THRESHOLD_X_VELOCITY) goToLeftWall();
                    else goToRightWall();
                }
            } else goToRightWall();
        }
    }

    private void goToRightWall() {
        animator.start(chatHeadLayoutWidth, bubbleLayout.getViewParams().y);
        bannerOnRightWall();
    }

    private void goToLeftWall() {
        animator.start(0, bubbleLayout.getViewParams().y);
        bannerOnLeftWall();
    }

    private void bannerOnRightWall() {
        bannerLayout.getViewParams().x = chatHeadLayoutWidth + (bubbleLayout.getWidth() / 2)
                - BubbleConstants.BUBBLE_BANNER_GAP_DP - bannerLayout.getWidth();
        bannerLayout.getViewParams().y = bubbleLayout.getViewParams().y;
        bannerLayout.setBackgroundResource(R.drawable.banner_right_bg);
        bannerLayout.getViewParams().gravity = Gravity.TOP | Gravity.RIGHT;
        updateBannerParams();
    }

    private void bannerOnLeftWall() {
        bannerLayout.getViewParams().x = bubbleLayout.getWidth() + BubbleConstants.BUBBLE_BANNER_GAP_DP;
        bannerLayout.getViewParams().y = bubbleLayout.getViewParams().y;
        bannerLayout.setBackgroundResource(R.drawable.banner_left_bg);
        bannerLayout.getViewParams().gravity = Gravity.TOP | Gravity.LEFT;
        updateBannerParams();
    }

    private void updateBannerParams() {
        bannerLayout.getViewParams().height = ActionBar.LayoutParams.WRAP_CONTENT;
        bannerLayout.getViewParams().width = ActionBar.LayoutParams.WRAP_CONTENT;
        bannerLayout.setVisibility(View.VISIBLE);
        windowManager.updateViewLayout(bannerLayout, bannerLayout.getViewParams());
    }

    private void initializeAnimator() {
        animator = new MoveAnimator();
    }

    private void chatHeadLayoutTouchEvent() {
        bubbleLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startVelocityTracking(event);
                            initialX = bubbleLayout.getViewParams().x;
                            initialY = bubbleLayout.getViewParams().y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            bubbleLayout.playAnimationClickDown();
                            lastTouchDown = System.currentTimeMillis();
                            updateSize();
                            animator.stop();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int x = initialX + (int) (event.getRawX() - initialTouchX);
                            int y = initialY + (int) (event.getRawY() - initialTouchY);
                            bubbleLayout.getViewParams().x = x;
                            bubbleLayout.getViewParams().y = y;
                            bubbleLayout.getWindowManager().updateViewLayout(v, bubbleLayout.getViewParams());
                            getTrackedVelocity(event);
                            notifyBubblePositionChanged();
                            hideBanner();
                            break;
                        case MotionEvent.ACTION_UP:
                            bubbleLayout.playAnimationClickUp();
                            if (System.currentTimeMillis() -
                                    lastTouchDown < BubbleConstants.CLICK_TIME_THRESHOLD) {
                                // register as click event do stuff
                                Toast.makeText(mContext, "clicked", Toast.LENGTH_LONG).show();
                            } else hideBanner();
                            goToWall(event);
                            notifyBubbleRelease();
                            recycleVelocityTracking();
                            break;
                        case MotionEvent.ACTION_OUTSIDE:
                            //handle here for checking outside touch
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            recycleVelocityTracking();
                            break;
                    }
                }
                return true;
            }
        });
    }

    private void startVelocityTracking(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
        velocityTracker.addMovement(event);
    }

    private void getTrackedVelocity(MotionEvent event) {
        velocityTracker.addMovement(event);
        xVelocity = velocityTracker.getXVelocity();
        //check if required or not. Do not understand.
        velocityTracker.computeCurrentVelocity(1000);
    }

    private void recycleVelocityTracking() {
        velocityTracker.recycle();
        velocityTracker = null;
    }

    private boolean checkIfBubbleIsOverTrash(BubbleLayout bubble) {
        boolean result = false;
        if (trashLayout.getVisibility() == View.VISIBLE) {
            View trashContentView = getTrashContent();
            int trashWidth = trashContentView.getMeasuredWidth();
            int trashHeight = trashContentView.getMeasuredHeight();
            int trashLeft = (trashContentView.getLeft() - (trashWidth / 2));
            int trashRight = (trashContentView.getLeft() + trashWidth + (trashWidth / 2));
            int trashTop = (trashContentView.getTop() - (trashHeight / 2));
            int trashBottom = (trashContentView.getTop() + trashHeight + (trashHeight / 2));
            int bubbleWidth = bubble.getMeasuredWidth();
            int bubbleHeight = bubble.getMeasuredHeight();
            int bubbleLeft = bubble.getViewParams().x;
            int bubbleRight = bubbleLeft + bubbleWidth;
            int bubbleTop = bubble.getViewParams().y;
            int bubbleBottom = bubbleTop + bubbleHeight;
            if (bubbleLeft >= trashLeft && bubbleRight <= trashRight) {
                if (bubbleTop >= trashTop && bubbleBottom <= trashBottom) {
                    result = true;
                }
            }
        }
        return result;
    }

    private void applyTrashMagnetismToBubble(BubbleLayout bubble) {
        View trashContentView = getTrashContent();
        int trashCenterX = (trashContentView.getLeft() + (trashContentView.getMeasuredWidth() / 2));
        int trashCenterY = (trashContentView.getTop() + (trashContentView.getMeasuredHeight() / 2));
        int x = (trashCenterX - (bubble.getMeasuredWidth() / 2));
        int y = (trashCenterY - (bubble.getMeasuredHeight() / 2));
        bubble.getViewParams().x = x;
        bubble.getViewParams().y = y;
        windowManager.updateViewLayout(bubble, bubble.getViewParams());
    }

    private void notifyBubblePositionChanged() {
        if (trashLayout != null) {
            trashLayout.setVisibility(View.VISIBLE);
            if (checkIfBubbleIsOverTrash(bubbleLayout)) {
                trashLayout.applyMagnetism();
                trashLayout.vibrate();
                applyTrashMagnetismToBubble(bubbleLayout);
            } else {
                trashLayout.releaseMagnetism();
            }
        }
    }

    private void notifyBubbleRelease() {
        if (trashLayout != null) {
            if (checkIfBubbleIsOverTrash(bubbleLayout)) {
                stopSelf();
            }
            trashLayout.setVisibility(View.GONE);
        }
    }

    private View getTrashContent() {
        return trashLayout.getChildAt(0);
    }

    /**
     * TODO: removing the notification if possible without making it background service
     * Notification manager does not work, kept for further testing
     * setSmallIcon should be a blank/transparent icon so notification icon does not appear
     */
    private void setAsForeground() {
        RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.bubble_notification_layout);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Bubbles")
                .setTicker("Bubbles")
                .setContentText("Bubbles")
                .setContent(notificationView)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true).build();

        startForeground(BubbleConstants.NOTIFICATION_ID, notification);

        /*NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIF_ID);*/
    }

    /**
     * why should the handler need mainLooper
     */
    private class MoveAnimator implements Runnable {
        //private Handler handler = new Handler(Looper.getMainLooper());
        private Handler handler = new Handler();
        private float destinationX;
        private float destinationY;
        private long startingTime;

        private void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        /***
         * need to understand this
         * what is speedFactor and the reason for it
         * also why it should be less than 1 for handler to post
         */
        @Override
        public void run() {
            if (bubbleLayout.getRootView() != null && bubbleLayout.getRootView().getParent() != null) {
                float speedFactor = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
                float deltaX = (destinationX - bubbleLayout.getViewParams().x) * speedFactor;
                float deltaY = (destinationY - bubbleLayout.getViewParams().y) * speedFactor;
                bubbleLayout.move(deltaX, deltaY);
                if (speedFactor < 1) {
                    handler.post(this);
                }
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }
}
