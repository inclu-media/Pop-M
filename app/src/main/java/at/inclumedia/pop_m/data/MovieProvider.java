package at.inclumedia.pop_m.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Martin Melcher on 06/11/15.
 */
@ContentProvider( authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {

    public static final String AUTHORITY = "at.inclumedia.pop_m";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String MOVIES     = "movies";
        String POPULAR    = "popular";
        String HIRATED    = "hirated";
        String FAVOURITE  = "favourite";
        String TRAILERS   = "trailers";
        String REVIEWS    = "reviews";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MovieDatabase.MOVIES)
    public static class Movies {

        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movies",
                defaultSort = MovieColumns.RATING + " DESC")
        public static final Uri ALL = buildUri(Path.MOVIES);

        @ContentUri(
                path = Path.MOVIES + "/" + Path.POPULAR,
                type = "vnd.android.cursor.dir/movies",
                where = MovieColumns.IS_POPULAR + " = 1",
                defaultSort = MovieColumns.RATING + " DESC")
        public static final Uri POPULAR = buildUri(Path.MOVIES, Path.POPULAR);

        @ContentUri(
                path = Path.MOVIES + "/" + Path.HIRATED,
                type = "vnd.android.cursor.dir/movies",
                where = MovieColumns.IS_HIRATED + " = 1",
                defaultSort = MovieColumns.RATING + " DESC")
        public static final Uri HIRATED = buildUri(Path.MOVIES, Path.HIRATED);

        @ContentUri(
                path = Path.MOVIES + "/" + Path.FAVOURITE,
                type = "vnd.android.cursor.dir/movies",
                where = MovieColumns.IS_FAVOURITE + " = 1",
                defaultSort = MovieColumns.RATING + " DESC")
        public static final Uri FAVOURITE = buildUri(Path.MOVIES, Path.FAVOURITE);


        @InexactContentUri(
                name = "MOVIE",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MovieDatabase.TRAILERS)
    public static class Trailers {

        @ContentUri(
                path = Path.TRAILERS,
                type = "vnd.android.cursor.dir/trailers")
        public static final Uri ALL = buildUri(Path.TRAILERS);

        @InexactContentUri(
                name = "TRAILERS_FOR_MOVIE",
                path = Path.TRAILERS + "/#",
                type = "vnd.android.cursor.dir/trailers",
                whereColumn = TrailerColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri forMovie(int id){
            return buildUri(Path.TRAILERS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MovieDatabase.REVIEWS)
    public static class Reviews {

        @ContentUri(
                path = Path.REVIEWS,
                type = "vnd.android.cursor.dir/reviews")
        public static final Uri ALL = buildUri(Path.REVIEWS);

        @InexactContentUri(
                name = "REVIEWS_FOR_MOVIE",
                path = Path.REVIEWS + "/#",
                type = "vnd.android.cursor.dir/reviews",
                whereColumn = ReviewColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri forMovie(int id){
            return buildUri(Path.REVIEWS, String.valueOf(id));
        }
    }
}