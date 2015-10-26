package at.inclumedia.pop_m;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * Created by boehni on 25/10/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, int resource, int imageViewResource) {
        super(context, resource, imageViewResource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_movie_image, parent, false);
        }

        // TODO: Add error and loading images for Picasso
        ImageView iconThumb = (ImageView) convertView.findViewById(R.id.view_movie_image);
        Picasso.with(parent.getContext()).load(movie.thumbUri).into(iconThumb);

        return convertView;
    }
}
