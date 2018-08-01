package sunny.quoteshake.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import sunny.quoteshake.R;

/**
 * Created by Wayan-MECS on 8/1/2018.
 */

public class NotificationBuilder extends AppCompatActivity {

    String TAG = "methode";

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;

    public void Notification(String fname, Activity activity) {

        Log.d(TAG, "notificationBuilder < 26");

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/QuoteShake/" + fname);
        intent.setDataAndType(Uri.fromFile(myDir), "image/*");

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        notificationBuilder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(logo)
                .setContentTitle("Quote Shake")
                .setContentText("Downloading File")
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public void notifClose() {
        Log.d(TAG, "notifClose");
        if(notificationManager!=null)
            notificationManager.cancel(0);
    }

}
