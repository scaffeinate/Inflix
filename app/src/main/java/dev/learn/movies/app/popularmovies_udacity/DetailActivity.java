package dev.learn.movies.app.popularmovies_udacity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sudharti on 10/11/17.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_NAME = "movie_name";

    private String movieName = null;
    private long movieId = 0L;

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private ImageView mToolbarIcon;
    private TextView mToolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarIcon = (ImageView) findViewById(R.id.imageview_toolbar_icon);
        mToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);

        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                movieId = bundle.getLong(MOVIE_ID, 0L);
                movieName = bundle.getString(MOVIE_NAME, "Detail");
            }
        } else {
            movieId = (savedInstanceState.containsKey(MOVIE_ID)) ? savedInstanceState.getLong(MOVIE_ID) : 0L;
            movieName = (savedInstanceState.containsKey(MOVIE_NAME) ? savedInstanceState.getString(MOVIE_NAME) : "Detail");
        }

        mToolbarTitle.setText(movieName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "" + movieId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MOVIE_ID, movieId);
        outState.putString(MOVIE_NAME, movieName);
    }
}
