package com.vibhor.android.circularprogressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private CircularProgressIndicator circularProgressIndicator;
    private EditText progressPercent;
    private Button animate;
    private int progress = 0;
    private int completedProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circularProgressIndicator = (CircularProgressIndicator) findViewById(R.id.progress_bar);
        // Max value of edit text can be 99. Which would mean, circle is complete
        progressPercent = (EditText) findViewById(R.id.edittext_progress_percent);
        animate = (Button) findViewById(R.id.button_animate);

        animate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressPercent.getText().toString().trim() != null) {
                    progress = Integer.valueOf(progressPercent.getText().toString().trim());
                    delayAndAnimate();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void delayAndAnimate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 99 would mean complete circle
                for (int i = 0; i < 100; i++) {
                    if(i >= progress)
                        break;
                    try {
                        Thread.sleep(40);
                        mHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                if (completedProgress < 100) {
                    completedProgress++;
                    circularProgressIndicator.setmProgress(completedProgress);
                    circularProgressIndicator.setOnProgressListener(new CircularProgressIndicator.ProgressEventListener() {
                        @Override
                        public void progressComplete() {
                        }
                    });
                }
            }
        }
    };
}
