package at.inclumedia.pop_m;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Martin Melcher on 25/10/15.
 */
public class MovieAdapter extends CursorAdapter {

    public static class ViewHolder {
        @Bind(R.id.view_movie_thumb) ImageView iconThumb;
        @Bind(R.id.textView_movie_caption) TextView tvCaption;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_movie_image, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Uri thumbUri = Uri.parse(cursor.getString(MovieGridActivityFragment.COL_MOVIE_THUMBURI));
        Picasso.with(context).load(thumbUri).error(R.drawable.notfound).into(viewHolder.iconThumb);

        String caption = cursor.getString(MovieGridActivityFragment.COL_MOVIE_TITLE);
        viewHolder.tvCaption.setText(caption);
    }

}
