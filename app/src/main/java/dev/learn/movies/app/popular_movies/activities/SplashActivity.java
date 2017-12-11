package dev.learn.movies.app.popular_movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import dev.learn.movies.app.popular_movies.R;

import static dev.learn.movies.app.popular_movies.Inflix.ACTIVITY_SPLASH_DELAY_IN_MS;

/**
 * Created by sudharti on 11/8/17.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, ACTIVITY_SPLASH_DELAY_IN_MS);
    }
}
