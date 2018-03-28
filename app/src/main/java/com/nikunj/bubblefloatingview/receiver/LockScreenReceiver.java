package com.nikunj.bubblefloatingview.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.nikunj.bubblefloatingview.bubbles.BubbleService;

public class LockScreenReceiver extends BroadcastReceiver {
    public LockScreenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
           /* Intent serviceIntent = new Intent(context, BubbleService.class);
            context.stopService(serviceIntent);*/
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Toast.makeText(context, "Screen On", Toast.LENGTH_LONG).show();
            Intent serviceIntent = new Intent(context, BubbleService.class);
            context.startService(serviceIntent);
           } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "Boot completed", Toast.LENGTH_LONG).show();
            Intent serviceIntent = new Intent(context, BubbleService.class);
            context.startService(serviceIntent);
        }
    }
}
