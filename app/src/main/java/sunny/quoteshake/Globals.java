package sunny.quoteshake;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import java.util.Random;


public class Globals extends AppCompatActivity {

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



}
