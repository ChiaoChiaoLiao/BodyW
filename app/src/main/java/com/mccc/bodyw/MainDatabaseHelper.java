package com.mccc.bodyw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Database";
    private static final int DB_VERSION = 1;

    public static final String TABLE = "main";

    public static final String COL_DATE = "DATE";
    public static final String COL_WEIGHT = "WEIGHT";
    public static final String COL_BODY_FAT = "BODY_FAT";

    MainDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                TABLE +
                " ( " +
                COL_DATE + " INTEGER PRIMARY KEY, " +
                COL_WEIGHT + " INTEGER, " +
                COL_BODY_FAT + " INTEGER " +
                " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
