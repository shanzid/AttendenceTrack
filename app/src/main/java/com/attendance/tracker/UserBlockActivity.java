package com.attendance.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class UserBlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_block);
        LottieAnimationView animationView
                = findViewById(R.id.animation_view);
        animationView
                .addAnimatorUpdateListener(
                        (animation) -> {
                            // Do something.
                        });
        animationView
                .playAnimation();

        if (animationView.isAnimating()) {
            // Do something.
        }

    }
}