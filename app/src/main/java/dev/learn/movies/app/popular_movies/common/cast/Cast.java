
package dev.learn.movies.app.popular_movies.common.cast;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dev.learn.movies.app.popular_movies.common.MediaDetail;

import static dev.learn.movies.app.popular_movies.data.DataContract.CastEntry.COLUMN_CHARACTER;
import static dev.learn.movies.app.popular_movies.data.DataContract.CastEntry.COLUMN_GENDER;
import static dev.learn.movies.app.popular_movies.data.DataContract.CastEntry.COLUMN_NAME;
import static dev.learn.movies.app.popular_movies.data.DataContract.CastEntry.COLUMN_ORDER;
import static dev.learn.movies.app.popular_movies.data.DataContract.CastEntry.COLUMN_PROFILE_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_MEDIA_ID;

public class Cast implements Parcelable {

    @SerializedName("cast_id")
    @Expose
    private long castId;
    @SerializedName("character")
    @Expose
    private String character;
    @SerializedName("credit_id")
    @Expose
    private String creditId;
    @SerializedName("gender")
    @Expose
    private long gender;
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("order")
    @Expose
    private long order;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;

    public Cast() {
    }

    protected Cast(Parcel in) {
        castId = in.readLong();
        character = in.readString();
        creditId = in.readString();
        gender = in.readLong();
        id = in.readLong();
        name = in.readString();
        order = in.readLong();
        profilePath = in.readString();
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    public long getCastId() {
        return castId;
    }

    public void setCastId(long castId) {
        this.castId = castId;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public long getGender() {
        return gender;
    }

    public void setGender(long gender) {
        this.gender = gender;
    }

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

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
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
        dest.writeLong(castId);
        dest.writeString(character);
        dest.writeString(creditId);
        dest.writeLong(gender);
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(order);
        dest.writeString(profilePath);
    }

    public static ContentValues toContentValues(Cast cast, long mediaId) {
        ContentValues cv = new ContentValues();
        if (cast != null) {
            cv.put(COLUMN_NAME, cast.getName());
            cv.put(COLUMN_GENDER, cast.getGender());
            cv.put(COLUMN_ORDER, cast.getOrder());
            cv.put(COLUMN_CHARACTER, cast.getCharacter());
            cv.put(COLUMN_PROFILE_PATH, cast.getProfilePath());
            cv.put(COLUMN_MEDIA_ID, mediaId);
        }
        return cv;
    }

    public static Cast fromCursor(Cursor cursor) {
        Cast cast = new Cast();
        if (cursor.moveToFirst()) {
            cast.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            cast.setGender(cursor.getLong(cursor.getColumnIndex(COLUMN_GENDER)));
            cast.setOrder(cursor.getLong(cursor.getColumnIndex(COLUMN_ORDER)));
            cast.setCharacter(cursor.getString(cursor.getColumnIndex(COLUMN_CHARACTER)));
            cast.setProfilePath(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_PATH)));
        }
        return cast;
    }
}
