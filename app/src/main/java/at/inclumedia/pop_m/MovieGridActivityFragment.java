package at.inclumedia.pop_m;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridActivityFragment extends Fragment {

    private static final String LOG_TAG = MovieGridActivityFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;

    public MovieGridActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // enable menu
        setHasOptionsMenu(true);


        // initialise custom ArrayAdapter for the grid view
        mMovieAdapter = new MovieAdapter (getActivity(), R.layout.view_movie_image, R.id.view_movie_image);
        GridView movieGridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        movieGridView.setAdapter(mMovieAdapter);

        // define click listener for grid items
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Context context = getActivity().getApplicationContext();
                Movie movie = mMovieAdapter.getItem(i);
                Intent detailIntent = new Intent(context, MovieDetailActivity.class);

                // put parcelable movie into intent
                detailIntent.putExtra("parcel_movie", movie);
                startActivity(detailIntent);
            }

        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String sFilter = sharedPref.getString(getString(R.string.prefs_sortorder_key), getString(R.string.prefs_sortorder_default));
        int iFilter = Integer.parseInt(sFilter);
        new FetchMoviesTask().execute(iFilter);
    }

    public class FetchMoviesTask extends AsyncTask<Integer, Void, ArrayList<Movie>> {

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movies);
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(Integer ... filters) {
            ArrayList<Movie> alMovies = TmdbAPI.getMovies(filters[0], getContext());
            return alMovies;
        }
    }
}
