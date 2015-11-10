package at.inclumedia.pop_m.mdbapi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import at.inclumedia.pop_m.R;

/**
 * Created by Martin Melcher on 23/10/15.
 */
public class API {

    private static final String LOG_TAG = API.class.getSimpleName();

    // filter values
    public static final int POPULAR_MOVIES = 0;
    public static final int HIGHRATED_MOVIES = 1;
    public static final int TRAILERS = 2;
    public static final int REVIEWS = 3;

    /**
     * Gets a list of popular movies from themoviedb.org
     *
     * @param context
     * @return the most popular movies
     */
    public static ArrayList<Movie> getPopularMovies(Context context) {
        ArrayList<Movie> movies = new ArrayList<>();
        JSONObject moviesJSON = requestMovies(POPULAR_MOVIES, 0, context);
        if (moviesJSON != null) {
            movies = getMoviesfromJSON(moviesJSON, context);
        }
        return movies;
    }

    /**
     * Gets a list of high rated movies from themoviedb.org
     *
     * @param context
     * @return the highest rated movies
     */
    public static ArrayList<Movie> getHiratedMovies(Context context) {
        ArrayList<Movie> movies = new ArrayList<>();
        JSONObject moviesJSON = requestMovies(HIGHRATED_MOVIES, 0, context);
        if (moviesJSON != null) {
            movies = getMoviesfromJSON(moviesJSON, context);
        }
        return movies;
    }

    /**
     * Gets a list of trailers for a given movie from themoviedb.org
     *
     * @param movieId
     * @param context
     * @return trailers for the movie
     */
    public static ArrayList<Trailer> getTrailers(int movieId, Context context) {
        ArrayList<Trailer> trailers = new ArrayList<>();
        JSONObject trailersJSON = requestMovies(TRAILERS, movieId, context);
        if (trailersJSON != null) {
            trailers = getTrailersfromJSON(trailersJSON, context);
        }
        return trailers;
    }

    /**
     * Gets a list of reviews for a given movie from themoviedb.org
     *
     * @param movieId
     * @param context
     * @return reviews for the movie
     */
    public static ArrayList<Review> getReviews(int movieId, Context context) {
        ArrayList<Review> reviews = new ArrayList<>();
        JSONObject reviewsJSON = requestMovies(REVIEWS, movieId, context);
        if (reviewsJSON != null) {
            reviews = getReviewsfromJSON(reviewsJSON, context);
        }
        return reviews;
    }

    private static ArrayList<Movie> getMoviesfromJSON(JSONObject json, Context context) {
        ArrayList<Movie> alMovies = new ArrayList<>();
        try {
            JSONArray arResults = json.getJSONArray(context.getString(R.string.tmdb_attrib_results));
            for (int i=0; i<arResults.length(); i++) {
                JSONObject objMovie = arResults.getJSONObject(i);
                Movie movie = new Movie(
                        objMovie.getString(context.getString(R.string.tmdb_attrib_title)),
                        getThumbUri(objMovie.getString(context.getString(R.string.tmdb_attrib_poster)), context),
                        objMovie.getString(context.getString(R.string.tmdb_attrib_overview)),
                        objMovie.getDouble(context.getString(R.string.tmdb_attrib_rating)),
                        objMovie.getString(context.getString(R.string.tmdb_attrib_date)),
                        objMovie.getInt("id")
                );
                alMovies.add(movie);
            }
        }
        catch (JSONException je) {
            Log.e(LOG_TAG, "Error", je);
        }
        return alMovies;
    }

    private static ArrayList<Trailer> getTrailersfromJSON(JSONObject json, Context context) {
        ArrayList<Trailer> alTrailers = new ArrayList<>();
        try {
            JSONArray arTrailers = json.getJSONArray(context.getString(R.string.tmdb_attrib_results));
            for (int i=0; i<arTrailers.length(); i++) {
                JSONObject objTrailer = arTrailers.getJSONObject(i);
                Trailer trailer = new Trailer(
                        objTrailer.getString(context.getString(R.string.tmdb_trailer_type)),
                        objTrailer.getString(context.getString(R.string.tmdb_trailer_name)),
                        getYouTubeUri(objTrailer.getString(context.getString(R.string.tmdb_trailer_key)), context)
                );
                alTrailers.add(trailer);
            }
        }
        catch (JSONException je) {
            Log.e(LOG_TAG, "Error", je);
        }
        return alTrailers;
    }

    private static ArrayList<Review> getReviewsfromJSON(JSONObject json, Context context) {
        ArrayList<Review> alReviews = new ArrayList<>();
        try {
            JSONArray arReviews = json.getJSONArray(context.getString(R.string.tmdb_attrib_results));
            for (int i=0; i<arReviews.length(); i++) {
                JSONObject objReview = arReviews.getJSONObject(i);
                Review review = new Review(
                        objReview.getString(context.getString(R.string.tmdb_review_author)),
                        objReview.getString(context.getString(R.string.tmdb_review_content))
                );
                alReviews.add(review);
            }
        }
        catch (JSONException je) {
            Log.e(LOG_TAG, "Error", je);
        }
        return alReviews;
    }

    private static Uri getThumbUri(String path, Context context) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.tmdb_schema))
                .authority(context.getString(R.string.tmdb_host_image))
                .appendEncodedPath(context.getString(R.string.tmdb_endpoint_image))
                .appendEncodedPath(path);
        return builder.build();
    }

    private static Uri getYouTubeUri(String key, Context context) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.tmdb_schema))
                .authority(context.getString(R.string.tmdb_trailer_host))
                .appendEncodedPath(key);
        return builder.build();
    }

    private static JSONObject requestMovies(int filterFlag, int movieId, Context context) {

        // construct API endpoint
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.tmdb_schema))
                .authority(context.getString(R.string.tmdb_host))
                .appendPath(context.getString(R.string.tmdb_version))
                .appendQueryParameter(context.getString(R.string.tmdb_apikey_key),
                                      context.getString(R.string.tmdb_apikey));
        switch (filterFlag) {
            case POPULAR_MOVIES: builder.appendEncodedPath(context.getString(R.string.tmdb_endpoint_discover));
                                 builder.appendQueryParameter(context.getString(R.string.tmdb_sortby),
                                                              context.getString(R.string.tmdb_sortby_popular));
                                 break;
            case HIGHRATED_MOVIES: builder.appendEncodedPath(context.getString(R.string.tmdb_endpoint_discover));
                                   builder.appendQueryParameter(context.getString(R.string.tmdb_sortby),
                                                                context.getString(R.string.tmdb_sortby_highrated));
                                   break;
            case TRAILERS: builder.appendEncodedPath(context.getString(R.string.tmdb_endpoint_movie));
                           builder.appendEncodedPath(Integer.toString(movieId));
                           builder.appendEncodedPath(context.getString(R.string.tmdb_endpoint_movie_trailers));
                           break;
            case REVIEWS: builder.appendEncodedPath(context.getString(R.string.tmdb_endpoint_movie));
                          builder.appendEncodedPath(Integer.toString(movieId));
                          builder.appendEncodedPath(context.getString(R.string.tmdb_endpoint_movie_reviews));
                          break;
            default:builder.appendEncodedPath(context.getString(R.string.tmdb_endpoint_discover));
                    builder.appendQueryParameter(context.getString(R.string.tmdb_sortby),
                                                 context.getString(R.string.tmdb_sortby_popular));
                    break;
        }

        // make API call
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(builder.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            JSONObject movieJsonObj = new JSONObject(buffer.toString());
            return movieJsonObj;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (JSONException je) {
            Log.e(LOG_TAG, "Error ", je);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }
}
