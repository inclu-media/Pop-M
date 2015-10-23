package at.inclumedia.pop_m;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by boehni on 23/10/15.
 */
public class TmdbAPI {

    private static final String LOG_TAG = TmdbAPI.class.getSimpleName();
    public static final int POPULAR_MOVIES = 0;
    public static final int HIGHRATED_MOVIES = 1;

    /**
     * Gets a list of movies from themoviedb.org depending
     * on a filter setting.
     *
     * @param filter POPULAR_MOVIES | HIGHRATED_MOVIES
     * @param context
     * @return the most popular movies
     */
    public static ArrayList<Movie> getMovies(int filter, Context context) {
        JSONObject moviesJSON = requestMovies(filter, context);
        ArrayList<Movie> movies = getMoviesfromJSON(moviesJSON);
        return movies;
    }

    private static ArrayList<Movie> getMoviesfromJSON(JSONObject json) {
        // TODO: parse json
        return null;
    }

    private static JSONObject requestMovies(int filterFlag, Context context) {

        // construct API endpoint
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.tmdb_schema))
                .authority(context.getString(R.string.tmdb_schema))
                .appendPath(context.getString(R.string.tmdb_host))
                .appendPath(context.getString(R.string.tmdb_version))
                .appendQueryParameter(context.getString(R.string.tmdb_apikey_key),
                                      context.getString(R.string.tmdb_apikey));
        switch (filterFlag) {
            case 0: builder.appendPath(context.getString(R.string.tmdb_endpoint_discover));
                    builder.appendQueryParameter(context.getString(R.string.tmdb_sortby),
                                                 context.getString(R.string.tmdb_sortby_popular));
                    break;
            case 1: builder.appendPath(context.getString(R.string.tmdb_endpoint_discover));
                    builder.appendQueryParameter(context.getString(R.string.tmdb_sortby),
                                                 context.getString(R.string.tmdb_sortby_highrated));
                    break;
            default:builder.appendPath(context.getString(R.string.tmdb_endpoint_discover));
                    builder.appendQueryParameter(context.getString(R.string.tmdb_sortby),
                                                 context.getString(R.string.tmdb_sortby_popular));
                    break;
        }
        Log.v(LOG_TAG, "TheMovieDB API Request URL" + builder.toString());

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
