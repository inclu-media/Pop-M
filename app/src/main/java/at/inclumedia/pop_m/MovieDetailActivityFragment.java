package at.inclumedia.pop_m;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.simonvt.schematic.annotation.DataType;

import at.inclumedia.pop_m.data.MovieColumns;
import at.inclumedia.pop_m.data.MovieProvider;
import at.inclumedia.pop_m.data.ReviewColumns;
import at.inclumedia.pop_m.data.TrailerColumns;
import at.inclumedia.pop_m.mdbapi.Movie;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.github.clans.fab.FloatingActionButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    private int mMovieId = -1;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    // loader
    private static final int DATA_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;

    // projections
    private static final String[] MOVIE_COLUMNS = {
            MovieColumns._ID,
            MovieColumns.TITLE,
            MovieColumns.RELEASE_DATE,
            MovieColumns.RATING,
            MovieColumns.THUMB_URL,
            MovieColumns.PLOT,
            MovieColumns.IS_FAVOURITE
    };
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_RELDATE = 2;
    static final int COL_MOVIE_RATING = 3;
    static final int COL_MOVIE_THUMBURL = 4;
    static final int COL_MOVIE_PLOT = 5;
    static final int COL_MOVIE_ISFAVOURITE = 6;

    private static final String[] TRAILER_COLUMNS = {
            TrailerColumns._ID,
            TrailerColumns.NAME,
            TrailerColumns.YOUTUBE_URL
    };
    static final int COL_TRAILER_NAME = 1;
    static final int COL_TRAILER_YOUTUBEURL = 2;

    private static final String[] REVIEW_COLUMNS = {
            ReviewColumns._ID,
            ReviewColumns.AUTHOR,
            ReviewColumns.CONTENT
    };
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    @Bind(R.id.textView_title) TextView tvTitle;
    @Bind(R.id.imageView_thumb) ImageView iconThumb;
    @Bind(R.id.textView_year) TextView tvRel;
    @Bind(R.id.textView_rating) TextView tvRating;
    @Bind(R.id.textView_plot) TextView tvPlot;
    @Bind(R.id.fabPlus) FloatingActionButton fabPlus;
    @Bind(R.id.fabMinus) FloatingActionButton fabMinus;
    @Bind(R.id.layout_trailers) LinearLayout lTrailers;
    @Bind(R.id.layout_reviews) LinearLayout lReviews;
    @Bind(R.id.scrollView_details) ScrollView svDetails;

    public MovieDetailActivityFragment() {
    }

    @OnClick(R.id.fabPlus)
    public void plusClicked() {
        if (mMovieId > -1) {
            ContentValues cvAddFav = new ContentValues();
            cvAddFav.put(MovieColumns.IS_FAVOURITE, 1);
            getActivity().getContentResolver().update(
                    MovieProvider.Movies.withId(mMovieId),
                    cvAddFav, null, null);

            Toast.makeText(getContext(), getString(R.string.fav_added), Toast.LENGTH_SHORT).show();
            fabMinus.setVisibility(View.VISIBLE);
            fabPlus.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.fabMinus)
    public void minusClicked() {
        if (mMovieId > -1) {
            ContentValues cvRemFav = new ContentValues();
            cvRemFav.put(MovieColumns.IS_FAVOURITE, 0);
            getActivity().getContentResolver().update(
                    MovieProvider.Movies.withId(mMovieId),
                    cvRemFav, null, null);

            Toast.makeText(getContext(), getString(R.string.fav_removed), Toast.LENGTH_SHORT).show();
            fabPlus.setVisibility(View.VISIBLE);
            fabMinus.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DATA_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        CursorLoader cl = null;
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        switch(i) {
            case DATA_LOADER:
                Uri providerUri = intent.getData();
                mMovieId = Integer.parseInt(providerUri.getLastPathSegment());
                cl = new CursorLoader(
                        getActivity(),
                        providerUri,
                        MOVIE_COLUMNS,
                        null, null, null);
                break;
            case TRAILER_LOADER:
                if (mMovieId == -1) {
                    return null;
                }
                cl =  new CursorLoader(
                        getActivity(),
                        MovieProvider.Trailers.forMovie(mMovieId),
                        TRAILER_COLUMNS,
                        null, null, COL_TRAILER_NAME + " ASC");
                break;
            case REVIEW_LOADER:
                if (mMovieId == -1) {
                    return null;
                }
                cl = new CursorLoader(
                        getActivity(),
                        MovieProvider.Reviews.forMovie(mMovieId),
                        REVIEW_COLUMNS,
                        null, null, null);
                break;
        }
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case DATA_LOADER:
                if (cursor.moveToFirst()) {

                    // plus or minus fav button
                    if (cursor.getInt(COL_MOVIE_ISFAVOURITE) == 1) {
                        fabPlus.setVisibility(View.INVISIBLE);
                        fabMinus.setVisibility(View.VISIBLE);
                    }
                    else {
                        fabPlus.setVisibility(View.VISIBLE);
                        fabMinus.setVisibility(View.INVISIBLE);
                    }

                    // fill UI
                    tvTitle.setText(cursor.getString(COL_MOVIE_TITLE));
                    Picasso.with(getActivity()).load(cursor.getString(COL_MOVIE_THUMBURL)).fit().error(R.drawable.ic_av_movie).centerCrop().into(iconThumb);
                    tvRel.setText(getReleaseYear(cursor.getString(COL_MOVIE_RELDATE)));
                    tvRating.setText(getRatingString(cursor.getDouble(COL_MOVIE_RATING)));
                    tvPlot.setText(cursor.getString(COL_MOVIE_PLOT));
                }
                break;
            case TRAILER_LOADER:
                mTrailerAdapter.swapCursor(cursor);
                lTrailers.removeAllViewsInLayout();
                for (int i=0; i<mTrailerAdapter.getCount(); i++) {
                    lTrailers.addView(mTrailerAdapter.getView(i, null, lTrailers));
                }
                break;
            case REVIEW_LOADER:
                // TODO: mReviewAdapter.swapCursor(cursor);
                break;
        }
        svDetails.scrollTo(0, 0); // move back up after adding things
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mTrailerAdapter.swapCursor(null);
        // TODO: mReviewAdapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        // create adapters
        mTrailerAdapter   = new TrailerAdapter(getActivity(), null, 0);
        // lvTrailers.setAdapter(mTrailerAdapter);
        // TODO: mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        // TODO: lvReviews.setAdapter(mReviewAdapter);

        return rootView;
    }


    private String getReleaseYear(String releaseDate) {
        String[] parts = releaseDate.split("-");
        return parts[0];
    }

    private String getRatingString(Double rating) {
        String sRat = Double.toString(rating);
        return sRat + "/" + getString(R.string.tmdb_maxrating);
    }
}
