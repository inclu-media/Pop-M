package at.inclumedia.pop_m;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        Movie movie  = intent.getParcelableExtra("parcel_movie");

        // populate title
        TextView tvTitle = (TextView) rootView.findViewById(R.id.textView_title);
        tvTitle.setText(movie.title);

        // populate thumb
        // TODO: Add error and loading images for Picasso
        ImageView iconThumb = (ImageView) rootView.findViewById(R.id.imageView_thumb);
        Picasso.with(getActivity()).load(movie.thumbUri).fit().centerCrop().into(iconThumb);

        // populate release year
        TextView tvRel = (TextView) rootView.findViewById(R.id.textView_year);
        tvRel.setText(getReleaseYear(movie.releaseDate));

        // populate rating
        TextView tvRating = (TextView) rootView.findViewById(R.id.textView_rating);
        tvRating.setText(getRatingString(movie.rating));

        // populate plot
        TextView tvPlot = (TextView) rootView.findViewById(R.id.textView_plot);
        tvPlot.setText(movie.plot);

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
