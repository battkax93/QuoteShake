package sunny.quoteshake;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

    private Dialog dialog;

    FirebaseDatabase fb;

    String TAG = "methode";

    String[] lottie = {"cycle_animation.json",
            "emoji wink.json",
            "loading_animation.json",
            "smiley_stack.json"};

    String[] picQuote2;

    ArrayList<String> picQuote = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        lottieAnimationView2 = findViewById(R.id.lottieAnimationView2);
        tvShake = findViewById(R.id.tv_shakeme);
        ivShakeme = findViewById(R.id.iv_shakeme);

        fb = FirebaseDatabase.getInstance();

        setLottie();
        fbRetriever();
        initShake();
        update();

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


    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        shakeDetector.destroy(getBaseContext());
        super.onDestroy();
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
                }
                handler.postDelayed(this, 30 * 1000);
            }
        });
    }

    public void showDialog(Activity activity) {

        Log.d("TAG", "showDialog");

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
