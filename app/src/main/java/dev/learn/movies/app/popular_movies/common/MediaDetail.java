package dev.learn.movies.app.popular_movies.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sudhar on 12/9/17.
 */

public class MediaDetail {

    @SerializedName("backdrop_path")
    @Expose
    protected String backdropPath;
    @SerializedName("id")
    @Expose
    protected long id;
    @SerializedName("overview")
    @Expose
    protected String overview;
    @SerializedName("poster_path")
    @Expose
    protected String posterPath;
    @SerializedName("vote_average")
    @Expose
    protected double voteAverage;
    @SerializedName("vote_count")
    @Expose
    protected long voteCount;
    @SerializedName("genres")
    @Expose
    protected List<Genre> genres;
    @SerializedName("status")
    @Expose
    protected String status;

    protected boolean isFavored = false;
    protected boolean isBookmarked = false;

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFavored() {
        return isFavored;
    }

    public void setFavored(boolean favored) {
        isFavored = favored;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}
