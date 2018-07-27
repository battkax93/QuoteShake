package sunny.quoteshake;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class MainActivity extends AppCompatActivity {

    ShakeDetector shakeDetector;
    TextView tvShake;
    int a;
    String[] quote = {"a", "b", "c", "d"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShake = findViewById(R.id.tv_shakeme);

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
                a = random();
                tvShake.setText(quote[a]);
                Toast.makeText(getApplicationContext(), "Shaken", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void update2() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (tvShake.getText().toString() != getString(R.string.shake_me)) {
                    tvShake.setText(getString(R.string.shake_me));
                }
            }
        }).start();
    }

    private int random() {
        int max = 3;
        int min = 0;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void update() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String s = tvShake.getText().toString();
                String x = getString(R.string.shake_me);
                if (s != x) {
                    tvShake.setText(getString(R.string.shake_me));
                }
                handler.postDelayed(this, 30 * 1000);
            }
        });
    }

    private void initVisibility() {
        if (tvShake.getVisibility() == View.GONE) {
            tvShake.setVisibility(View.VISIBLE);
        } else {
            tvShake.setVisibility(View.GONE);
        }
    }

}
