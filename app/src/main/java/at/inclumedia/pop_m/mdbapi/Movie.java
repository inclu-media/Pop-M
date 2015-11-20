package at.inclumedia.pop_m.mdbapi;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Martin Melcher on 23/10/15.
 */
public class Movie implements Parcelable {

    public String title;
    public Uri thumbUri;
    public Uri backdropUri;
    public String plot;
    public double rating;
    public String releaseDate;
    public int mdbId;

    public Movie(String title, Uri thumbUri, Uri backdropUri, String plot, double rating, String releaseDate, int mdbId) {
        this.title = title;
        this.thumbUri = thumbUri;
        this.backdropUri = backdropUri;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.mdbId = mdbId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(thumbUri.toString());
        dest.writeString(backdropUri.toString());
        dest.writeString(plot);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeInt(mdbId);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        title       = in.readString();
        thumbUri    = Uri.parse(in.readString());
        backdropUri = Uri.parse(in.readString());
        plot        = in.readString();
        rating      = in.readDouble();
        releaseDate = in.readString();
        mdbId       = in.readInt();
    }
}
