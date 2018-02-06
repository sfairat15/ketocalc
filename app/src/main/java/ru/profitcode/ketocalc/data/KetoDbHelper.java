package ru.profitcode.ketocalc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.profitcode.ketocalc.data.ProductContract.ProductEntry;

/**
 * Database helper for Products app. Manages database creation and version management.
 */
public class KetoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = KetoDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "ketocalc.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 3;

    /**
     * Constructs a new instance of {@link KetoDbHelper}.
     *
     * @param context of the app
     */
    public KetoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_DROP_PRODUCTS_TABLE =  "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + ";";
        db.execSQL(SQL_DROP_PRODUCTS_TABLE);

        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PROTEIN + " REAL NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_FAT + " REAL NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_CARBO + " REAL NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_TAG + " INTEGER NULL);";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
        onCreate(db);
    }
}