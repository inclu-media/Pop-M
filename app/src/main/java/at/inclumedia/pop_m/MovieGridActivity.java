package at.inclumedia.pop_m;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MovieGridActivity extends AppCompatActivity implements MovieGridActivityFragment.Callback {

    private boolean mTwoPane;
    static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        // tablet layout
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailActivityFragment.DETAIL_URI, movieUri);
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }
}
