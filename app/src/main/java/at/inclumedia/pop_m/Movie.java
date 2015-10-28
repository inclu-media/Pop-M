package at.inclumedia.pop_m;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Martin Melcher on 23/10/15.
 */
public class Movie implements Parcelable {

    String title;
    Uri thumbUri;
    String plot;
    double rating;
    String releaseDate;

    public Movie(String title, Uri thumbUri, String plot, double rating, String releaseDate) {
        this.title = title;
        this.thumbUri = thumbUri;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(thumbUri.toString());
        dest.writeString(plot);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
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
        plot        = in.readString();
        rating      = in.readDouble();
        releaseDate = in.readString();
    }
}
