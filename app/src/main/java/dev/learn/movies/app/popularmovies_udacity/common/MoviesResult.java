
package dev.learn.movies.app.popularmovies_udacity.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    private List<Movie> results = null;

    public long getPage() {
        return page;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }

}
