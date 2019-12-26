package id.web.realtimetracking.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import id.web.realtimetracking.FriendRequestActivity;
import id.web.realtimetracking.R;

public class NotificationHelper extends ContextWrapper {
    private static final String KINARYATAMA_RAHARJA_ID ="id.web.realtimetracking";
    private static final String KINARYATAMA_RAHARJA_NAME = "Realtime2019";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel kinaryatama = new NotificationChannel(KINARYATAMA_RAHARJA_ID,
                KINARYATAMA_RAHARJA_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        kinaryatama.enableLights(false);
        kinaryatama.enableVibration(true);
        kinaryatama.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        gettManager().createNotificationChannel(kinaryatama);
    }

    public NotificationManager gettManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackingNotification(String tittle, String content, Uri defaultSound) {



        return new Notification.Builder(getApplicationContext(),KINARYATAMA_RAHARJA_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(tittle)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);
    }
}
