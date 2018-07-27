package sunny.quoteshake;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import java.util.Random;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class MainActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView, lottieAnimationView2;
    ShakeDetector shakeDetector;
    TextView tvShake;
    ImageView ivShakeme;
    int a, x;

    private Dialog dialog;

    String TAG = "methode";

    String[] lottie = {"cycle_animation.json",
            "emoji wink.json",
            "little girl.json",
            "Ping Pong.json"};

    String[] img = {
            "https://i.pinimg.com/originals/b3/68/2e/b3682e52a71afcf05ba038d85c22352b.jpg",
            "https://i.pinimg.com/originals/68/fb/9b/68fb9bb6beb6b1af3b6ccfed1ca37cb6.jpg",
            "https://i.pinimg.com/564x/7f/2c/6d/7f2c6d38714b77c26ee00896c9fa0725.jpg",
            "https://i.pinimg.com/236x/34/7e/8b/347e8b6a6cccc91c5d8dcadb743cc7f8.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"onCreate");

        lottieAnimationView2 = findViewById(R.id.lottieAnimationView2);
        tvShake = findViewById(R.id.tv_shakeme);
        ivShakeme = findViewById(R.id.iv_shakeme);

        setLottie();

        initShake();
        update();

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

                Log.d(TAG,"shaken");

                a = random();
                String url = img[a];
                lottieAnimationView2.setVisibility(View.GONE);
                tvShake.setVisibility(View.GONE);
                ivShakeme.setVisibility(View.VISIBLE);
                Picasso.get().load(url)
                        .error(R.drawable.nointernet)
                        .into(ivShakeme);

            }
        });
    }

    private int random() {

        Log.d(TAG,"random");

        int max = 3;
        int min = 0;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void setLottie() {

        Log.d(TAG,"setLottie");

        x = random();
        lottieAnimationView2.setAnimation(lottie[x]);
        lottieAnimationView2.playAnimation();
        lottieAnimationView2.loop(true);
    }

    private void update() {
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

    private void showDialog(Activity activity) {

        Log.d("TAG", "showDialog");
        int x = random();

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.progress_dialog);

        lottieAnimationView = dialog.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation(lottie[x]);
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);

        dialog.show();
    }

}
