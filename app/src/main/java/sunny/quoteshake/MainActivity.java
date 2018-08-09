package sunny.quoteshake;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    TextView tvShake, tvTips, tvTips2;
    ImageView ivShakeme, btnShare, btnDownload;
    LinearLayout L1, L2;
    Boolean cekL2 = false;

    public String url, fname;
    int a, x, z;
    int count = 1;

    public Dialog dialog;

    FirebaseDatabase fb;
    AdView adView;

    String[] lottie;

    String[] picQuote2;
    ArrayList<String> picQuote = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logHelper("onCreate");

        init();

        fb = FirebaseDatabase.getInstance();

        setLottie();
        fbRetriever();
        initShake();
        initadMobs();
        update();
    }

    @Override
    protected void onDestroy() {
        logHelper("onDestroy");
        shakeDetector.destroy(getBaseContext());
        super.onDestroy();
    }

    private void init() {
        adView = findViewById(R.id.adView_main);
        lottieAnimationView2 = findViewById(R.id.lottieAnimationView2);
        tvShake = findViewById(R.id.tv_shakeme);
        tvTips = findViewById(R.id.tv_tips);
        tvTips2 = findViewById(R.id.tv_tips2);
        ivShakeme = findViewById(R.id.iv_shakeme);
        btnDownload = findViewById(R.id.btn_download2);
        btnShare = findViewById(R.id.btn_share2);

        L1 = findViewById(R.id.L1);
        L2 = findViewById(R.id.L2);

        lottie = getResources().getStringArray(R.array.lottie);

        ivShakeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqPermission();
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogFBAnalytic(MainActivity.this,
                        Contants.ID_BUTTON_DOWNLOAD,
                        Contants.TYPE_BUTTON,
                        Contants.NAME_BUTTON_DOWNLOAD);
                imageDownload();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogFBAnalytic(MainActivity.this,
                        Contants.ID_BUTTON_SHARE,
                        Contants.TYPE_BUTTON,
                        Contants.NAME_BUTTON_SHARE);
                shareBitmap();
            }
        });

    }

    private void initShake() {

        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(2000)
                .shakeCount(3)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
            @Override
            public void onShake() {

                logHelper("shaken");
                sendLogFBAnalytic(MainActivity.this,
                        Contants.ID_SHAKE,
                        Contants.TYPE_SHAKE,
                        Contants.NAME_SHAKE
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

    public void initadMobs() {

        MobileAds.initialize(this, Contants.ADD_MOB_APP_ID);             //adMob

        if (!Contants.admob) {
            logHelper("admob DEV");
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("6B7C8118873F959A250EF2732E708691")
                    .build();

            adView.loadAd(adRequest);
        } else {
            logHelper("admob PROD");
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

    public void fbRetriever() {
        logHelper("fbRetriever");
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
                        logHelper("add " + singlesnapshot.getValue(String.class));
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

    public void setPicQuote2() {
        a = random();
        url = picQuote2[a];
        lottieAnimationView2.setVisibility(View.GONE);
        tvShake.setVisibility(View.GONE);
        adView.setVisibility(View.GONE);
        L1.setVisibility(View.VISIBLE);
        Picasso.get().load(url)
                .error(R.drawable.nointernet)
                .into(ivShakeme);
    }

    public int random() {

        logHelper("random");

        int max = z;
        Random r = new Random();
        return r.nextInt(max);
    }

    public void setLottie() {

        logHelper("setLottie");

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

                logHelper("update");

                if (lottieAnimationView2.getVisibility() == View.GONE) {
                    L1.setVisibility(View.GONE);
                    L2.setVisibility(View.GONE);
                    cekL2 = false;
                    adView.setVisibility(View.VISIBLE);
                    lottieAnimationView2.setVisibility(View.VISIBLE);
                    tvShake.setVisibility(View.VISIBLE);
                    setLottie();
                }
                handler.postDelayed(this, 60 * 1000);
            }
        });
    }

    public void reqPermission() {
        logHelper("reqPermission");
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

//                        Bitmap bmp = ((BitmapDrawable) ivShakeme.getDrawable()).getBitmap();
//                        showDialog2(bmp);
                        if (!cekL2) {
                            cekL2 = true;
                            L2.setVisibility(View.VISIBLE);
                            tvTips.setVisibility(View.GONE);
                            tvTips2.setVisibility(View.GONE);
                        } else {
                            cekL2 = false;
                            L2.setVisibility(View.GONE);
                            tvTips.setVisibility(View.VISIBLE);
                            tvTips2.setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }

    public void shareBitmap() {

        Log.d(TAG, "shareBitmap");

        Bitmap bmp = ((BitmapDrawable) ivShakeme.getDrawable()).getBitmap();

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "title", null);
        Uri bitmapUri = Uri.parse(bitmapPath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    public void imageDownload() {

        String n = String.valueOf(System.currentTimeMillis());
        fname = "QuoteShake-" + n + ".jpg";

        logHelper("imageDownload");
        Bitmap bitmap = ((BitmapDrawable) ivShakeme.getDrawable()).getBitmap();

        if (Build.VERSION.SDK_INT >= 26) {
            save2(bitmap, fname, count);
            count++;
        } else {
            save1(bitmap, fname, this, count);
            count++;
        }

    }

    public void showDialog(Activity activity) {

        logHelper("showDialog");

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

   /* public void showDialog2(final Bitmap bmp) {

        logHelper("showdialog2");

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

                shareBitmap();
            }
        });


        dialog2.show();

    }*/

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
