
package com.example.a23_db_list;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.util.Log;

public class ListLoader extends CursorLoader {

    private Context mContext;

    public ListLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor c = null;
        SQLiteDatabase db = DBAdapter.getInstance(mContext).dbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            c = db.query(DBAdapter.TABLE_NAME, null, null, null, null, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB Error", e.toString());
        } finally {
            db.endTransaction();
        }
        return c;
    }
}
