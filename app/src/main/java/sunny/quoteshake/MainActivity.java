package sunny.quoteshake;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    int a, x, z;

    public Dialog dialog, dialog2;

    FirebaseDatabase fb;

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
        update();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        shakeDetector.destroy(getBaseContext());
        super.onDestroy();
    }

    private void init() {
        lottieAnimationView2 = findViewById(R.id.lottieAnimationView2);
        tvShake = findViewById(R.id.tv_shakeme);
        ivShakeme = findViewById(R.id.iv_shakeme);

        lottie = getResources().getStringArray(R.array.lottie);

        ivShakeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog2();
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
        String url = picQuote2[a];
        lottieAnimationView2.setVisibility(View.GONE);
        tvShake.setVisibility(View.GONE);
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
                    lottieAnimationView2.setVisibility(View.VISIBLE);
                    tvShake.setVisibility(View.VISIBLE);
                    setLottie();

                    if (dialog2.isShowing()) {
                        dialog2 = new Dialog(MainActivity.this);
                        dialog2.cancel();
                    }
                }
                handler.postDelayed(this, 30 * 1000);
            }
        });
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
