package at.inclumedia.pop_m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;

import at.inclumedia.pop_m.data.MovieColumns;
import at.inclumedia.pop_m.data.MovieProvider;
import at.inclumedia.pop_m.mdbapi.API;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieGridActivityFragment.class.getSimpleName();
    private static final int SHOW_POPULAR = 0;
    private static final int SHOW_HIRATED = 1;
    private static final int SHOW_FAVOURITE = 2;
    private int currentlyShown = SHOW_POPULAR;

    // loaders
    private static final int POPULAR_LOADER = 0;
    private static final int HIRATED_LOADER = 1;
    private static final int FAVOURITE_LOADER = 2;

    private MovieAdapter mMoviesAdapter;

    // projection
    private static final String[] MOVIE_COLUMNS = {
            MovieColumns._ID,
            MovieColumns.THUMB_URL
    };
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_THUMBURI = 1;

    @Bind(R.id.movies_grid_view) GridView movieGridView;

    @OnItemClick(R.id.movies_grid_view)
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(pos);
        if (cursor != null) {

            // trailers and reviews for that movie
            int movieId = cursor.getInt(COL_MOVIE_ID);
            MovieStoreManager msm = MovieStoreManager.getInstance(getContext());
            msm.updateTrailersForMovie(movieId);
            msm.updateReviewsForMovie(movieId);

            // create intent
            Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
            detailIntent.setData(MovieProvider.Movies.withId(movieId));
            startActivity(detailIntent);
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener sortOrderListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                    // without checking isAdded it may crash -> Fragment not attached to Activity
                    if (isAdded() && key.equals(getString(R.string.prefs_sortorder_key))) {
                        int value = Integer.parseInt(prefs.getString(key, getString(R.string.prefs_sortorder_default)));
                        MovieGridActivityFragment.this.currentlyShown = value;
                        MovieGridActivityFragment.this.getLoaderManager().restartLoader(
                                value,
                                null,
                                MovieGridActivityFragment.this);
                    }
                }
            };

    public MovieGridActivityFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                         .registerOnSharedPreferenceChangeListener(sortOrderListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // wanted to do this onPause but the listener still needs to be available when the
        // settings activity is displayed (during which the fragment is paused).
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                         .unregisterOnSharedPreferenceChangeListener(sortOrderListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_LOADER, null, this);
        getLoaderManager().initLoader(HIRATED_LOADER, null, this);
        getLoaderManager().initLoader(FAVOURITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri providerUri;
        switch(i) {
            case POPULAR_LOADER:
                providerUri = MovieProvider.Movies.POPULAR;
                break;
            case HIRATED_LOADER:
                providerUri = MovieProvider.Movies.HIRATED;
                break;
            case FAVOURITE_LOADER:
                providerUri = MovieProvider.Movies.FAVOURITE;
                break;
            default:
                providerUri = MovieProvider.Movies.POPULAR;
                break;
        }

        return new CursorLoader(
                getActivity(),
                providerUri,
                MOVIE_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursorLoader.getId() == currentlyShown) {
            mMoviesAdapter.swapCursor(cursor);
        }
        cursorLoader.abandon(); // stop onLoadFinished from firing multiple times
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMoviesAdapter.swapCursor(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // pull movies via api into db
        // TODO: check for internet connection
        updateMovies();

        // initialise what's to be shown
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String sFilter = sharedPref.getString(getString(R.string.prefs_sortorder_key), getString(R.string.prefs_sortorder_default));
        currentlyShown = Integer.parseInt(sFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        ButterKnife.bind(this, rootView);

        // enable menu
        setHasOptionsMenu(true);

        // create adapters
        mMoviesAdapter = new MovieAdapter(getActivity(), null, 0);
        movieGridView.setAdapter(mMoviesAdapter);

        // set number of columns
        // could work with auto_fit here but this would give me only 2 columns in landscape on a mobile
        movieGridView.setNumColumns(getActivity().getResources().getInteger(R.integer.num_columns));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_grid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // launch settings
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(), MovieSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        FetchMoviesTask fmt = new FetchMoviesTask();
        fmt.execute(0);
    }

    public class FetchMoviesTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer ... filters) {
            ArrayList popularMovies = API.getPopularMovies(getContext());
            ArrayList hiratedMovies = API.getHiratedMovies(getContext());
            MovieStoreManager.getInstance(getContext()).savePopularMovies(popularMovies);
            MovieStoreManager.getInstance(getContext()).saveHiratedMovies(hiratedMovies);
            return null;
        }
    }
}
