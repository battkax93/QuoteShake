package sunny.quoteshake.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

import sunny.quoteshake.BuildConfig;
import sunny.quoteshake.R;

/**
 * Created by Wayan-MECS on 7/4/2018.
 */

public class NotificationHelper extends ContextWrapper {
    private NotificationManager mManager;
    //    public static final String ANDROID_CHANNEL_ID = "id.co.adira.ad1mobileakses.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {

        NotificationChannel androidChannel = new NotificationChannel(getPackageName(), ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        androidChannel.enableLights(true);
        androidChannel.enableVibration(true);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    public Notification.Builder getAndroidChannelNotification(String title, String body, String nm) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/QuoteShake/" + nm);
        Uri pdfURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+ ".provider", myDir);

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        target.setDataAndType(pdfURI, "image/*");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        List<ResolveInfo> infos = this.getPackageManager().queryIntentActivities(target, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : infos) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, pdfURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(pdfURI, "image/*");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);

        return new Notification.Builder(getApplicationContext(), getPackageName())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(logo)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
    }
}