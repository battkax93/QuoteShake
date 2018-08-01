package sunny.quoteshake;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import sunny.quoteshake.notification.NotificationBuilder;
import sunny.quoteshake.notification.NotificationHelper;


public class Globals extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    String TAG = "methode";


    public void toastHelper(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int randomLottie() {
        Log.d(TAG, "randomLottie");

        int max = 4;
        int min = 0;
        Random r = new Random();
        return r.nextInt(max);
    }

    public void sendLogFBAnalytic(Activity activity, String id, String name, String contentType) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void save1(Bitmap bmp, String fname, Activity activity) {
        Log.d(TAG, "saveImage<26");

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/QuoteShake");
        myDir.mkdirs();

        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            NotificationBuilder nb = new NotificationBuilder();
            nb.Notification(fname,activity );
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            Log.d(TAG, "Succes saving image");
            toastHelper("Yoo, your image has been saved");

            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText("QR CODE Downloaded");
            notificationManager.notify(1, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void save2(Bitmap bmp, String fname) {

        Log.d(TAG, "saveImage>26");

        String contentName = "Downloading Image";

        String root = Environment.getExternalStorageDirectory().toString();
        File docFile = new File(root + "/QuoteShake");
        docFile.mkdirs();

        File file = new File(docFile, fname);
        if (file.exists())
            file.delete();
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationHelper notificationHelper = new NotificationHelper(this);
                Notification.Builder nb = notificationHelper.getAndroidChannelNotification(getString(R.string.quote_shake), contentName, fname);
                notificationHelper.getManager().notify(1, nb.build());

                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                nb.setProgress(100, 0, false);
                nb = notificationHelper.getAndroidChannelNotification(getString(R.string.quote_shake), "Image Downloaded", fname);
                notificationHelper.getManager().notify(1, nb.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri pdfURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.replace(".dev", "") + ".provider", docFile);
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
    }

    public void saveImage3(String url, Bitmap bmp) {

        Log.d(TAG, "saveImage<26");
        String fname;

        String n = String.valueOf(System.currentTimeMillis());
        fname = "Image-" + n + ".jpg";
        String contentName = "Downloading";
        String root = Environment.getExternalStorageDirectory().toString();

        File docFile = new File(root + "/QuoteShake");
        docFile.mkdirs();
        File file = new File(docFile, fname);
        if (file.exists())
            file.delete();
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationHelper notificationHelper = new NotificationHelper(this);
                Notification.Builder nb = notificationHelper.getAndroidChannelNotification(getString(R.string.quote_shake), contentName, fname);
                notificationHelper.getManager().notify(1, nb.build());

                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                nb.setProgress(100, 0, false);
                nb = notificationHelper.getAndroidChannelNotification(getString(R.string.quote_shake), "QR CODE Downloaded", fname);
                notificationHelper.getManager().notify(1, nb.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri pdfURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.replace(".dev", "") + ".provider", docFile);
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
    }

}
