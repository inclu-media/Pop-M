package at.inclumedia.pop_m;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;

import at.inclumedia.pop_m.data.MovieColumns;
import at.inclumedia.pop_m.data.MovieProvider;
import at.inclumedia.pop_m.data.ReviewColumns;
import at.inclumedia.pop_m.data.TrailerColumns;
import at.inclumedia.pop_m.mdbapi.API;
import at.inclumedia.pop_m.mdbapi.Movie;
import at.inclumedia.pop_m.mdbapi.Review;
import at.inclumedia.pop_m.mdbapi.Trailer;

/**
 * Created by Martin Melcher on 08/11/15.
 */
public class MovieStoreManager {

    private static MovieStoreManager theManager;
    private static Context mContext;

    // filter values
    public static final int POPULAR_MOVIES = 0;
    public static final int HIGHRATED_MOVIES = 1;
    public static final int FAVOURITE_MOVIES = 2;

    // extras
    public static final int TRAILERS = 0;
    public static final int REVIEWS = 1;

    private MovieStoreManager() {}

    public static MovieStoreManager getInstance(Context context) {
        mContext = context;
        if (theManager == null) {
            theManager = new MovieStoreManager();
        }
        return theManager;
    }

    public void savePopularMovies(ArrayList<Movie> movies) {
        saveMovies(movies, POPULAR_MOVIES);
    }

    public void saveHiratedMovies(ArrayList<Movie> movies) {
        saveMovies(movies, HIGHRATED_MOVIES);
    }

    public void updateTrailersForMovie(int movieId) {
        new FetchExtrasTask().execute(TRAILERS, movieId);
    }

    public void updateReviewsForMovie(int movieId) {
        new FetchExtrasTask().execute(REVIEWS, movieId);
    }

    private void updateExtras(int flag, int movieId) {
        // delete all trailers or reviews for movie
        switch (flag) {
            case TRAILERS:
                mContext.getContentResolver().delete(
                        MovieProvider.Trailers.ALL,
                        TrailerColumns.MOVIE_ID + " = ?",
                        new String[]{Integer.toString(movieId)});
                break;
            case REVIEWS:
                mContext.getContentResolver().delete(
                        MovieProvider.Reviews.ALL,
                        ReviewColumns.MOVIE_ID + " = ?",
                        new String[]{Integer.toString(movieId)});
                break;
            default: break;
        }

        // fetch new trailers or reviews
        switch(flag) {
            case TRAILERS:
                ArrayList arTrailers = API.getTrailers(movieId, mContext);
                saveTrailers(movieId, arTrailers);
                break;
            case REVIEWS:
                ArrayList arReviews = API.getReviews(movieId, mContext);
                saveReviews(movieId, arReviews);
                break;
            default: break;
        }
    }


    private void saveTrailers(int movieId, ArrayList<Trailer> trailers) {
        for (int i=0; i<trailers.size(); i++) {
            Trailer trailer = trailers.get(i);

            ContentValues cvTrailer = new ContentValues();
            cvTrailer.put(TrailerColumns.MOVIE_ID, movieId);
            cvTrailer.put(TrailerColumns.NAME, trailer.name);
            cvTrailer.put(TrailerColumns.TYPE, trailer.type);
            cvTrailer.put(TrailerColumns.YOUTUBE_URL, trailer.youTubeUri.toString());

            mContext.getContentResolver().insert(
                    MovieProvider.Trailers.ALL,
                    cvTrailer);
        }
    }

    private void saveReviews(int movieId, ArrayList<Review> reviews) {
        for (int i=0; i<reviews.size(); i++) {
            Review review = reviews.get(i);

            ContentValues cvReview = new ContentValues();
            cvReview.put(ReviewColumns.MOVIE_ID, movieId);
            cvReview.put(ReviewColumns.AUTHOR, review.author);
            cvReview.put(ReviewColumns.CONTENT, review.content);

            mContext.getContentResolver().insert(
                    MovieProvider.Reviews.ALL,
                    cvReview);
        }
    }

    private void saveMovies(ArrayList<Movie> movies, int filter) {

        // prepare DB for new popular and high-rated movies
        clearFlags(filter);

        for (int i=0; i<movies.size(); i++) {
            Movie theMovie = movies.get(i);

            ContentValues cvMovie = new ContentValues();
            cvMovie.put(MovieColumns.TITLE, theMovie.title);
            cvMovie.put(MovieColumns.RELEASE_DATE, theMovie.releaseDate);
            cvMovie.put(MovieColumns.THUMB_URL, theMovie.thumbUri.toString());
            cvMovie.put(MovieColumns.PLOT, theMovie.plot);
            cvMovie.put(MovieColumns.RATING, theMovie.rating);

            switch (filter) {
                case POPULAR_MOVIES:
                    cvMovie.put(MovieColumns.IS_POPULAR, 1);
                    break;
                case HIGHRATED_MOVIES:
                    cvMovie.put(MovieColumns.IS_HIRATED, 1);
                    break;
            }

            Cursor c = mContext.getContentResolver().query(
                    MovieProvider.Movies.ALL,
                    null,
                    "_ID = ?",
                    new String[]{Integer.toString(theMovie.mdbId)},
                    null);
            if (c.getCount() == 1) {
                mContext.getContentResolver().update(
                        MovieProvider.Movies.ALL,
                        cvMovie,
                        "_ID = ?",
                        new String[]{Integer.toString(theMovie.mdbId)});
            }
            else {
                cvMovie.put(MovieColumns._ID, theMovie.mdbId);
                mContext.getContentResolver().insert(
                        MovieProvider.Movies.ALL,
                        cvMovie);
            }
            c.close();
        }
    }

    private void clearFlags(int filter) {
        ContentValues cvFlags = new ContentValues();

        switch (filter) {
            case POPULAR_MOVIES:   cvFlags.put(MovieColumns.IS_POPULAR, 0); break;
            case HIGHRATED_MOVIES: cvFlags.put(MovieColumns.IS_HIRATED, 0); break;
        }
        mContext.getContentResolver().update(
                MovieProvider.Movies.ALL,
                cvFlags,
                null,
                null);
    }

    private class FetchExtrasTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer ... flags) {
            updateExtras(flags[0], flags[1]);
            return null;
        }
    }
}
