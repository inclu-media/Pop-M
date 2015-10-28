package at.inclumedia.pop_m;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Martin Melcher on 25/10/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, ArrayList<Movie> alMovies) {
        super(context, 0, alMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_movie_image, parent, false);
        }
        ImageView iconThumb = (ImageView) convertView.findViewById(R.id.view_movie_image);
        Picasso.with(parent.getContext()).load(movie.thumbUri).error(R.drawable.notfound).into(iconThumb);

        return convertView;
    }
}
