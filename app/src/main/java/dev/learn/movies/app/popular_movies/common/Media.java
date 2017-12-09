package dev.learn.movies.app.popular_movies.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhar on 12/9/17.
 */

public class Media {
    @SerializedName("id")
    @Expose
    protected long id;

    @SerializedName("poster_path")
    @Expose
    protected String posterPath;

    public long getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
