
package com.example.a23_db_list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private ListDataAdapter mListAdapter;

    private DBAdapter mDBAdapter;
    
    private ListView mListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListview = (ListView)findViewById(R.id.list);
        setAdapters();
        getSupportLoaderManager().restartLoader(0, null, mLoaderCallbacks);
    }

    private void setAdapters() {
        mListAdapter = new ListDataAdapter(getApplicationContext());
        mDBAdapter = new DBAdapter(getApplicationContext());
        mListview.setAdapter(mListAdapter);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new ListLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mListAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private class ListDataAdapter extends CursorAdapter {

        private final LayoutInflater mInflater;

        class ViewHolder {
            TextView textRank;

            TextView textTitle;

            TextView textArtist;

            TextView textText;
        }

        public ListDataAdapter(Context context) {
            super(context, null, false);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void bindView(View v, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder)v.getTag();

            int rank = cursor.getInt(cursor.getColumnIndex(DBAdapter.COL_ID));
            String title = cursor.getString(cursor.getColumnIndex(DBAdapter.COL_TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(DBAdapter.COL_ARTIST));
            String text = cursor.getString(cursor.getColumnIndex(DBAdapter.COL_TEXT));

            holder.textRank.setText(rank + "位");
            holder.textTitle.setText(title);
            holder.textArtist.setText(artist);
            holder.textText.setText(text);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.row, null);
            ViewHolder holder = new ViewHolder();
            holder.textRank = (TextView)view.findViewById(R.id.text_rank);
            holder.textTitle = (TextView)view.findViewById(R.id.text_title);
            holder.textArtist = (TextView)view.findViewById(R.id.text_artist);
            holder.textText = (TextView)view.findViewById(R.id.text_text);
            view.setTag(holder);
            return view;
        }

    }

}
