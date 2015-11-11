package at.inclumedia.pop_m;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by boehni on 10/11/15.
 */
public class TrailerAdapter extends CursorAdapter {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    @Bind(R.id.imageView_youtube_thumb) ImageView ivYoutube;
    @Bind(R.id.textView_trailer_title) TextView tvHeading;
    @Bind(R.id.imageButton_play) ImageButton ibPlay;

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_movie_trailer, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ButterKnife.bind(this, view);

        tvHeading.setText(cursor.getString(MovieDetailActivityFragment.COL_TRAILER_NAME));

        // get youtube preview image and display
        final String youTubeUrl = cursor.getString(MovieDetailActivityFragment.COL_TRAILER_YOUTUBEURL);
        final Uri youTubeUri = Uri.parse(youTubeUrl);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.tmdb_schema))
                .authority(context.getString(R.string.tmdb_trailer_imghost))
                .appendEncodedPath(context.getString(R.string.tmdb_trailer_path))
                .appendEncodedPath(youTubeUri.getLastPathSegment())
                .appendEncodedPath(context.getString(R.string.tmdb_trailer_file));
        Uri youTubeThumbUri = builder.build();
        Picasso.with(context).load(youTubeThumbUri).fit().centerCrop().into(ivYoutube);

        // youtube intent
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireTube(youTubeUri, view);
            }

            private void fireTube(Uri youTubeUri, View view){
                try{
                    String key = youTubeUri.getLastPathSegment();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                    view.getContext().startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    Intent intent = new Intent(Intent.ACTION_VIEW, youTubeUri);
                    view.getContext().startActivity(intent);
                }
            }
        });

    }
}
