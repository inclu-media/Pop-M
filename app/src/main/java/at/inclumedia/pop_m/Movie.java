package at.inclumedia.pop_m;

import android.net.Uri;
import java.util.Date;

/**
 * Created by boehni on 23/10/15.
 */
public class Movie {

    String title;
    Uri thumbUri;
    String plot;
    int rating;
    Date releaseDate;

    public Movie(String title, Uri thumbUri, String plot, int rating, Date releaseDate) {
        this.title = title;
        this.thumbUri = thumbUri;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }
}
