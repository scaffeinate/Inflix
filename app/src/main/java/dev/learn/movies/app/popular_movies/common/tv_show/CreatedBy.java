
package dev.learn.movies.app.popular_movies.common.tv_show;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatedBy implements Parcelable {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gender")
    @Expose
    private long gender;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;

    public CreatedBy(String name) {
        this.name = name;
    }

    protected CreatedBy(Parcel in) {
        id = in.readLong();
        name = in.readString();
        gender = in.readLong();
        profilePath = in.readString();
    }

    public static final Creator<CreatedBy> CREATOR = new Creator<CreatedBy>() {
        @Override
        public CreatedBy createFromParcel(Parcel in) {
            return new CreatedBy(in);
        }

        @Override
        public CreatedBy[] newArray(int size) {
            return new CreatedBy[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGender() {
        return gender;
    }

    public void setGender(long gender) {
        this.gender = gender;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(gender);
        dest.writeString(profilePath);
    }
}
