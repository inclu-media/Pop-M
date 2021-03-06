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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import at.inclumedia.pop_m.data.MovieColumns;
import at.inclumedia.pop_m.data.MovieProvider;
import at.inclumedia.pop_m.data.ReviewColumns;
import at.inclumedia.pop_m.data.TrailerColumns;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.github.clans.fab.FloatingActionButton;

public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private Uri mUri;
    private int mMovieId = -1;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private ShareActionProvider mShareActionProvider;
    private String mTitle = null;
    private String mYouTubeUrl = null;


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
            MovieColumns.IS_FAVOURITE,
            MovieColumns.BACKDROP_URL
    };
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_RELDATE = 2;
    static final int COL_MOVIE_RATING = 3;
    static final int COL_MOVIE_THUMBURL = 4;
    static final int COL_MOVIE_PLOT = 5;
    static final int COL_MOVIE_ISFAVOURITE = 6;
    static final int COL_MOVIE_BACKDROPURL = 7;

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
    @Bind(R.id.textView_plot) TextView tvPlot;
    @Bind(R.id.fabPlus) FloatingActionButton fabPlus;
    @Bind(R.id.fabMinus) FloatingActionButton fabMinus;
    @Bind(R.id.layout_trailers) LinearLayout lTrailers;
    @Bind(R.id.layout_reviews) LinearLayout lReviews;
    @Bind(R.id.scrollView_details) ScrollView svDetails;
    @Bind(R.id.textView_trailer_heading) TextView tvTrailerHeading;
    @Bind(R.id.textView_reviews_heading) TextView tvReviewHeading;
    @Bind(R.id.view_trailer_divider) View vTrailerDivider;
    @Bind(R.id.view_review_divider) View vReviewDivider;
    @Bind(R.id.relativeLayout_detail) RelativeLayout rlDetails;
    @Bind(R.id.imageView_backdrop) ImageView ivBackdrop;
    @Bind(R.id.ratingBar) RatingBar rbRating;

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
        Uri providerUri = mUri;
        if (providerUri == null) {
            return null;
        }
        switch(i) {
            case DATA_LOADER:
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
                        null, null, COL_REVIEW_AUTHOR + " ASC");
                break;
        }
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case DATA_LOADER:
                if (cursor.moveToFirst()) {

                    rlDetails.setVisibility(View.VISIBLE);

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
                    Picasso.with(getActivity()).load(cursor.getString(COL_MOVIE_BACKDROPURL)).fit().centerCrop().into(ivBackdrop);
                    mTitle = cursor.getString(COL_MOVIE_TITLE);
                    tvTitle.setText(mTitle + " (" +getReleaseYear(cursor.getString(COL_MOVIE_RELDATE))+ ")");
                    rbRating.setMax(10);
                    rbRating.setRating(cursor.getFloat(COL_MOVIE_RATING));
                    tvPlot.setText(cursor.getString(COL_MOVIE_PLOT));
                }
                break;
            case TRAILER_LOADER:
                if (cursor.getCount() > 0) {
                    mTrailerAdapter.swapCursor(cursor);
                    lTrailers.removeAllViewsInLayout();
                    tvTrailerHeading.setVisibility(View.VISIBLE);
                    lTrailers.setVisibility(View.VISIBLE);
                    vTrailerDivider.setVisibility(View.VISIBLE);
                    for (int i = 0; i < mTrailerAdapter.getCount(); i++) {
                        lTrailers.addView(mTrailerAdapter.getView(i, null, lTrailers));
                    }
                    cursor.moveToFirst();
                    mYouTubeUrl = cursor.getString(COL_TRAILER_YOUTUBEURL);
                }
                else {
                    tvTrailerHeading.setVisibility(View.GONE);
                    lTrailers.setVisibility(View.GONE);
                    vTrailerDivider.setVisibility(View.GONE);
                    mYouTubeUrl = null;
                }
                break;
            case REVIEW_LOADER:
                if (cursor.getCount() > 0) {
                    mReviewAdapter.swapCursor(cursor);
                    lReviews.removeAllViewsInLayout();
                    tvReviewHeading.setVisibility(View.VISIBLE);
                    lReviews.setVisibility(View.VISIBLE);
                    vReviewDivider.setVisibility(View.VISIBLE);
                    for (int i = 0; i < mReviewAdapter.getCount(); i++) {
                        lReviews.addView(mReviewAdapter.getView(i, null, lReviews));
                    }
                }
                else {
                    tvReviewHeading.setVisibility(View.GONE);
                    lReviews.setVisibility(View.GONE);
                    vReviewDivider.setVisibility(View.GONE);
                }
                break;
        }
        // now that everything is loaded
        setShareIntent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mTrailerAdapter.swapCursor(null);
        mReviewAdapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        // two column layout
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        // enable menu
        setHasOptionsMenu(true);

        // create adapters
        mTrailerAdapter   = new TrailerAdapter(getActivity(), null, 0);
        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    private String getReleaseYear(String releaseDate) {
        String[] parts = releaseDate.split("-");
        return parts[0];
    }

    private void setShareIntent() {
        String msg  = getString(R.string.popm_share_discover, mTitle);
        if (mYouTubeUrl != null) {
            msg += " " + getString(R.string.popm_share_trailer, mYouTubeUrl);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.setType("text/plain");

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(intent);
        }
    }
}
