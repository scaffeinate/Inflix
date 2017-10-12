package dev.learn.movies.app.popularmovies_udacity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sudharti on 10/11/17.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID = "movie_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
}
