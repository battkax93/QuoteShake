package sunny.quoteshake;

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

        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
            @Override
            public void onShake() {
                a = random();
                if (a == 1) {
//                    initVisibility();
                    tvShake.setText(quote[a - 1]);
                } else if (a == 2) {
                    tvShake.setText(quote[a - 1]);
                } else if (a == 3) {
                    tvShake.setText(quote[a - 1]);
                } else {
                    tvShake.setText(quote[a - 1]);
                }
                Toast.makeText(getApplicationContext(), "Shaken", Toast.LENGTH_LONG).show();
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

    private int random() {
        int max = 4;
        int min = 1;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
