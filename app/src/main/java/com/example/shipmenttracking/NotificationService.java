package com.example.shipmenttracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private SharedPreferences preferences;
    private static final String PREF_KEY = Objects.requireNonNull(MainActivity.class.getPackage()).toString();
    private Timer timer;
    private TimerTask timerTask;
    private int ID;
    private Long time;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Notification", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        time = System.currentTimeMillis()/1000;

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i("Notification", "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.i("Notification", "onDestroy");
        stoptimertask();
        super.onDestroy();
    }

    final Handler handler = new Handler();

    /**
     * startTimer init-nel push es 12 orankent
     *
     * @return void
     */
    public void startTimer() {
        timer = new Timer();

        initializeTimerTask();

        time = System.currentTimeMillis()/1000;

        if(((preferences.getLong("lastNotificationTime", 0)+43200) <= time) && (preferences.getLong("lastNotificationTime",0) != 0)) {
            timer.schedule(timerTask, 5000, 43200 * 1000);
        } else if(preferences.getLong("lastNotificationTime",0) == 0) {
            timer.schedule(timerTask, 0, 5000);
        }
    }

    /**
     * stoptimertask a nev magaert beszel
     *
     * @return void
     */
    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * timer init
     *
     * @return void
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Log.i("Notification","PUSHED TIMED NOTIFICATION");
                        preferences.edit().putLong("lastNotificationTime", time).apply();
                        stoptimertask();
                        showNotification(getString(R.string.app_name), "It's time to track your order!");
                    }
                });
            }
        };
    }

    /**
     * notification push
     *
     * @return void
     */
    void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"com.example.shipmenttracking")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("com.example.shipmenttracking");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "com.example.shipmenttracking",
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(ID++, builder.build());
    }

}