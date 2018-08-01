package sunny.quoteshake;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class MainActivity extends Globals {

    LottieAnimationView lottieAnimationView, lottieAnimationView2;
    ShakeDetector shakeDetector;
    TextView tvShake;
    ImageView ivShakeme;
    public String url, fname;
    int a, x, z;

    public Dialog dialog, dialog2;

    FirebaseDatabase fb;
    AdView adView;

    String TAG = "methode";

    String[] lottie;

    String[] picQuote2;
    ArrayList<String> picQuote = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        init();

        fb = FirebaseDatabase.getInstance();

        setLottie();
        fbRetriever();
        initShake();
        adMobs();
        update();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        shakeDetector.destroy(getBaseContext());
        super.onDestroy();
    }

    private void init() {
        adView = findViewById(R.id.adView_main);
        lottieAnimationView2 = findViewById(R.id.lottieAnimationView2);
        tvShake = findViewById(R.id.tv_shakeme);
        ivShakeme = findViewById(R.id.iv_shakeme);

        lottie = getResources().getStringArray(R.array.lottie);

        ivShakeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqPermission();
            }
        });
    }

    public void fbRetriever() {
        Log.d(TAG, "fbRetriever");
        if (isNetworkAvailable()) {
            showDialog(this);
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference myRef = database.child("img/");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long size = dataSnapshot.getChildrenCount();
                    z = size.intValue();
                    picQuote2 = new String[z];
                    for (DataSnapshot singlesnapshot : dataSnapshot.getChildren()) {
                        picQuote.add(singlesnapshot.getValue(String.class));
                        Log.d(TAG, "add " + singlesnapshot.getValue(String.class));
                    }

                    picQuote2 = picQuote.toArray(picQuote2);
                    dialog.cancel();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Hi Buddy, please check your connection", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });
        } else {
            toastHelper(getString(R.string.conn_reminder));
        }
    }

    public void adMobs() {

        MobileAds.initialize(this, Contants.ADD_MOB_APP_ID);             //adMob

        if (!Contants.admob) {
            Log.d(TAG, "admob DEV");
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("6B7C8118873F959A250EF2732E708691")
                    .build();

            adView.loadAd(adRequest);
        } else {
            Log.d(TAG, "admob PROD");
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdClosed() {
                    Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                    Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });

            adView.loadAd(adRequest);
        }
    }

    private void initShake() {

        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
            @Override
            public void onShake() {

                Log.d(TAG, "shaken");
                sendLogFBAnalytic(MainActivity.this,
                        Contants.ID_SHAKE,
                        Contants.NAME_SHAKE,
                        Contants.TYPE_SHAKE
                );

                if (picQuote2 != null) {

                    setPicQuote2();

                } else {
                    if (isNetworkAvailable()) {
                        fbRetriever();
                    } else {
                        toastHelper(getString(R.string.conn_reminder));
                    }
                }
            }
        });
    }

    public void setPicQuote2() {
        a = random();
        url = picQuote2[a];
        lottieAnimationView2.setVisibility(View.GONE);
        tvShake.setVisibility(View.GONE);
        adView.setVisibility(View.GONE);
        ivShakeme.setVisibility(View.VISIBLE);
        Picasso.get().load(url)
                .error(R.drawable.nointernet)
                .into(ivShakeme);
    }

    public int random() {

        Log.d(TAG, "random");

        int max = z;
        Random r = new Random();
        return r.nextInt(max);
    }

    public void setLottie() {

        Log.d(TAG, "setLottie");

        x = randomLottie();
        lottieAnimationView2.setAnimation(lottie[x]);
        lottieAnimationView2.playAnimation();
        lottieAnimationView2.loop(true);
    }

    public void update() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "update");

                if (lottieAnimationView2.getVisibility() == View.GONE) {
                    ivShakeme.setVisibility(View.GONE);
                    adView.setVisibility(View.VISIBLE);
                    lottieAnimationView2.setVisibility(View.VISIBLE);
                    tvShake.setVisibility(View.VISIBLE);
                    setLottie();
                }
                handler.postDelayed(this, 30 * 1000);
            }
        });
    }

    public void imageDownload() {

        String n = String.valueOf(System.currentTimeMillis());
        fname = "QuoteShake-" + n + ".jpg";

        Log.d(TAG, "imageDownload");
        Bitmap bitmap = ((BitmapDrawable) ivShakeme.getDrawable()).getBitmap();

        if (Build.VERSION.SDK_INT >= 26) {
            save2(bitmap, fname);
        } else {
            save1(bitmap, fname, this);
        }
    }

    public void reqPermission() {
        Log.d(TAG,"reqPermission");
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showDialog2();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }

    public void showDialog(Activity activity) {

        Log.d(TAG, "showDialog");

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.progress_dialog);

        lottieAnimationView = dialog.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation("Ping Pong.json");
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);

        dialog.show();
    }

    public void showDialog2() {

        Log.d(TAG, "showdialog2");

        dialog2 = new Dialog(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog2.setCancelable(true);
        dialog2.setContentView(R.layout.download_dialog);

        Button dialogButton2 = dialog2.findViewById(R.id.btn_download);
        Button dialogButton = dialog2.findViewById(R.id.btn_share);

        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogFBAnalytic(MainActivity.this,
                        Contants.ID_BUTTON_DOWNLOAD,
                        Contants.NAME_BUTTON_DOWNLOAD,
                        Contants.TYPE_BUTTON);

                imageDownload();
            }
        });

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogFBAnalytic(MainActivity.this,
                        Contants.ID_BUTTON_SHARE,
                        Contants.NAME_BUTTON_SHARE,
                        Contants.TYPE_BUTTON);
            }
        });

        dialog2.show();

    }

/*
    private void update3() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                setLottie();
                handler.postDelayed(this, 30 * 1000);
            }
        });
    }
*/
}
