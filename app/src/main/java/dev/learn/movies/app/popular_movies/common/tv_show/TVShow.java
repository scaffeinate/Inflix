
package dev.learn.movies.app.popular_movies.common.tv_show;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dev.learn.movies.app.popular_movies.common.Media;

public class TVShow extends Media implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;

    protected TVShow(Parcel in) {
        id = in.readLong();
        posterPath = in.readString();
        name = in.readString();
    }

    public static final Creator<TVShow> CREATOR = new Creator<TVShow>() {
        @Override
        public TVShow createFromParcel(Parcel in) {
            return new TVShow(in);
        }

        @Override
        public TVShow[] newArray(int size) {
            return new TVShow[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(posterPath);
        dest.writeString(name);
    }
}
