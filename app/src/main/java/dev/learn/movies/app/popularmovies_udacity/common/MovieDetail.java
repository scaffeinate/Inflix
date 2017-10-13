
package dev.learn.movies.app.popularmovies_udacity.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetail {


    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("runtime")
    @Expose
    private long runtime;

    @SerializedName("tagline")
    @Expose
    private String tagline;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("vote_average")
    @Expose
    private double voteAverage;

    @SerializedName("vote_count")
    @Expose
    private long voteCount;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres;

    public String getBackdropPath() {
        return backdropPath;
    }

    public long getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public long getRuntime() {
        return runtime;
    }

    public String getTagline() {
        return tagline;
    }

    public String getTitle() {
        return title;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public List<Genre> getGenres() {
        return genres;
    }
}
