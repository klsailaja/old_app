package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BlinkHandler implements Runnable {

    private final List<TextView> componentsList;
    private ScheduledFuture<?> blinkTaskHandler;
    private final int blinkColor1 = Color.parseColor("white");
    private final int blinkColor2 = Color.parseColor("red");
    private boolean flip;
    private final Context context;

    public BlinkHandler(Context context, List<TextView> componentsList) {
        this.context = context;
        this.componentsList = componentsList;
        init();
    }

    private void init() {
        Scheduler scheduler = Scheduler.getInstance();
        blinkTaskHandler = scheduler.submitRepeatedTask(this, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        if (blinkTaskHandler != null) {
            blinkTaskHandler.cancel(true);
        }
    }

    public void run() {
        try {
            if (componentsList == null) {
                return;
            }
            flip = !flip;
            int currentColor = blinkColor2;
            if (!flip) {
                currentColor = blinkColor1;
            }
            final int finalColorCode = currentColor;
            Runnable run = () -> {
                for (int index = 0; index < componentsList.size(); index ++) {
                    TextView textView = componentsList.get(index);
                    textView.setTextColor(finalColorCode);
                }
            };
            if (context != null) {
                ((Activity)context).runOnUiThread(run);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
