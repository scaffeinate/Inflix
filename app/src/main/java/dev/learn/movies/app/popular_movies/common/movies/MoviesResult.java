package dev.learn.movies.app.popular_movies.common.movies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.learn.movies.app.popular_movies.common.Media;

@SuppressWarnings("unused")
public class MoviesResult {

    @SerializedName("page")
    @Expose
    private long page;
    @SerializedName("total_results")
    @Expose
    private long totalResults;
    @SerializedName("total_pages")
    @Expose
    private long totalPages;
    @SerializedName("results")
    @Expose
    private List<Media> results = null;

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<Media> getResults() {
        return results;
    }

    public void setResults(List<Media> results) {
        this.results = results;
    }
}
