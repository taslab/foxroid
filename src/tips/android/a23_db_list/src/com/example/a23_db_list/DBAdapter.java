
package com.example.a23_db_list;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DBAdapter {

    static final String DATABASE_NAME = "rank.db";

    static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "rank";

    public static final String COL_ID = "_id";

    public static final String COL_TITLE = "title";

    public static final String COL_ARTIST = "artist";

    public static final String COL_TEXT = "text";

    protected final Context mContext;

    protected final DatabaseHelper dbHelper;

    private static DBAdapter dbAdapter;

    public DBAdapter(Context context) {
        mContext = context;
        dbHelper = new DatabaseHelper(mContext);
    }

    public static DBAdapter getInstance(Context context) {
        if (dbAdapter == null) {
            dbAdapter = new DBAdapter(context);
        }
        return dbAdapter;
    }

    public class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                execSql(mContext, db, "sql/create");
                execSql(mContext, db, "sql/datas");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.beginTransaction();
            try {
                execSql(mContext, db, "sql/drop");

                execSql(mContext, db, "sql/create");
                execSql(mContext, db, "sql/datas");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

    }

    public DBAdapter open() {
        dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public static void execSql(Context context, SQLiteDatabase db, String assetsDir) {
        AssetManager as = context.getResources().getAssets();
        try {
            String files[] = as.list(assetsDir);
            for (int i = 0; i < files.length; i++) {
                String str = readFile(as.open(assetsDir + "/" + files[i]));
                for (String sql : str.split(";")) {
                    if (!TextUtils.isEmpty(sql) && !"\n".equals(sql)) {
                        db.execSQL(sql);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(InputStream is) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
            return sb.toString();
        } finally {
            if (br != null)
                br.close();
        }
    }
}
