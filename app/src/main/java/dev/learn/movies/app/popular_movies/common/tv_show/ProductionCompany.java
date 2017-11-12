
package dev.learn.movies.app.popular_movies.common.tv_show;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductionCompany implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private long id;

    protected ProductionCompany(Parcel in) {
        name = in.readString();
        id = in.readLong();
    }

    public static final Creator<ProductionCompany> CREATOR = new Creator<ProductionCompany>() {
        @Override
        public ProductionCompany createFromParcel(Parcel in) {
            return new ProductionCompany(in);
        }

        @Override
        public ProductionCompany[] newArray(int size) {
            return new ProductionCompany[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(id);
    }
}
