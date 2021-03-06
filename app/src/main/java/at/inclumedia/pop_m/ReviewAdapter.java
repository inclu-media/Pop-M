package at.inclumedia.pop_m;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Martin Melcher on 10/11/15.
 */
public class ReviewAdapter extends CursorAdapter {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public static class ViewHolder {
        @Bind(R.id.textView_review_author) TextView tvAuthor;
        @Bind(R.id.textView_review_content) TextView tvContent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_movie_review, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.tvAuthor.setText(cursor.getString(MovieDetailActivityFragment.COL_REVIEW_AUTHOR));
        viewHolder.tvContent.setText(cursor.getString(MovieDetailActivityFragment.COL_REVIEW_CONTENT));

    }
}
