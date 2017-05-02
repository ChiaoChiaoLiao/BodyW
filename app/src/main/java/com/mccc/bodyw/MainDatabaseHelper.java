package com.mccc.bodyw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MainDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Database";
    private static final int DB_VERSION = 1;

    public static final String TABLE = "main";

    public static abstract class RecordEntry implements BaseColumns {
        public static final String TABLE_NAME = "main";
        public static final String COL_DATE = "DATE";
        public static final String COL_WEIGHT = "WEIGHT";
        public static final String COL_BODY_FAT = "BODY_FAT";
    }

    public static class RecordQuery {
        public static final int DATE = 0;
        public static final int WEIGHT = 1;
        public static final int BODY_FAT = 2;
    }

    public static String[] getRecordQueryProjection() {
        return new String[]{
                MainDatabaseHelper.RecordEntry.COL_DATE,
                MainDatabaseHelper.RecordEntry.COL_WEIGHT,
                MainDatabaseHelper.RecordEntry.COL_BODY_FAT
        };
    }

    MainDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                TABLE +
                " ( " +
                RecordEntry.COL_DATE + " INTEGER PRIMARY KEY, " +
                RecordEntry.COL_WEIGHT + " INTEGER, " +
                RecordEntry.COL_BODY_FAT + " INTEGER " +
                " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
