package ru.profitcode.ketocalc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.profitcode.ketocalc.data.KetoContract.ProductEntry;
import ru.profitcode.ketocalc.data.KetoContract.SettingsEntry;
import ru.profitcode.ketocalc.data.KetoContract.ReceiptEntry;
import ru.profitcode.ketocalc.data.KetoContract.DishEntry;

/**
 * Database helper for Ketocalc app. Manages database creation and version management.
 */
public class KetoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = KetoDbHelper.class.getSimpleName();

    /** Name of the database file */
    public static final String DATABASE_NAME = "ketocalc.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 8;

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

        String SQL_DROP_SETTINGS_TABLE =  "DROP TABLE IF EXISTS " + SettingsEntry.TABLE_NAME + ";";
        db.execSQL(SQL_DROP_SETTINGS_TABLE);

        String SQL_CREATE_SETTINGS_TABLE =  "CREATE TABLE " + SettingsEntry.TABLE_NAME + " ("
                + SettingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SettingsEntry.COLUMN_SETTINGS_NAME + " TEXT NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT + " INTEGER NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_FRACTION + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_CALORIES + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_PROTEINS + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1 + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2 + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3 + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4 + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5 + " REAL NOT NULL, "
                + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6 + " REAL NOT NULL); ";

        db.execSQL(SQL_CREATE_SETTINGS_TABLE);

        String SQL_DROP_RECEIPTS_TABLE =  "DROP TABLE IF EXISTS " + ReceiptEntry.TABLE_NAME + ";";
        db.execSQL(SQL_DROP_RECEIPTS_TABLE);

        String SQL_CREATE_RECEIPTS_TABLE =  "CREATE TABLE " + ReceiptEntry.TABLE_NAME + " ("
                + ReceiptEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReceiptEntry.COLUMN_RECEIPT_NAME + " TEXT NOT NULL, "
                + ReceiptEntry.COLUMN_RECEIPT_MEAL + " INTEGER NULL, "
                + ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS + " INTEGER NULL, "
                + ReceiptEntry.COLUMN_RECEIPT_NOTE + " INTEGER NULL);";

        db.execSQL(SQL_CREATE_RECEIPTS_TABLE);

        String SQL_CREATE_DISHES_TABLE =  "CREATE TABLE " + DishEntry.TABLE_NAME + " ("
                + DishEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DishEntry.COLUMN_DISH_NAME + " TEXT NOT NULL, "
                + DishEntry.COLUMN_DISH_INGREDIENTS + " INTEGER NULL, "
                + DishEntry.COLUMN_DISH_NOTE + " INTEGER NULL);";

        db.execSQL(SQL_CREATE_DISHES_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //for new installations
        if(oldVersion < 6) {
            onCreate(db);
        }

        if(newVersion >= 7 && oldVersion == 6)
        {
            String SQL_CREATE_DISHES_TABLE =  "CREATE TABLE " + DishEntry.TABLE_NAME + " ("
                    + DishEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DishEntry.COLUMN_DISH_NAME + " TEXT NOT NULL, "
                    + DishEntry.COLUMN_DISH_INGREDIENTS + " INTEGER NULL, "
                    + DishEntry.COLUMN_DISH_NOTE + " INTEGER NULL);";

            db.execSQL(SQL_CREATE_DISHES_TABLE);
        }

        if(newVersion == 8 && oldVersion == 7)
        {
            db.beginTransaction();
            try {
                String SQL_RENAME_SETTINGS_TO_TMP_TABLE =  "ALTER TABLE " + SettingsEntry.TABLE_NAME
                        + "  RENAME TO tmp_settings;";

                db.execSQL(SQL_RENAME_SETTINGS_TO_TMP_TABLE);

                String SQL_CREATE_SETTINGS_TABLE =  "CREATE TABLE " + SettingsEntry.TABLE_NAME + " ("
                        + SettingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + SettingsEntry.COLUMN_SETTINGS_NAME + " TEXT NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT + " INTEGER NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_FRACTION + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_CALORIES + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_PROTEINS + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1 + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2 + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3 + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4 + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5 + " REAL NOT NULL, "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6 + " REAL NOT NULL); ";

                db.execSQL(SQL_CREATE_SETTINGS_TABLE);

                String SQL_FILL_SETTINGS_TABLE =  "INSERT INTO " + SettingsEntry.TABLE_NAME +
                        " ("
                            + SettingsEntry.COLUMN_SETTINGS_NAME + ", "
                            + SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT + ", "
                            + SettingsEntry.COLUMN_SETTINGS_FRACTION + ", "
                            + SettingsEntry.COLUMN_SETTINGS_CALORIES + ", "
                            + SettingsEntry.COLUMN_SETTINGS_PROTEINS + ", "
                            + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1 + ", "
                            + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2 + ", "
                            + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3 + ", "
                            + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4 + ", "
                            + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5 + ", "
                            + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6 + ") "
                            + " SELECT "
                        + SettingsEntry.COLUMN_SETTINGS_FRACTION + ", "
                        + "1, "
                        + SettingsEntry.COLUMN_SETTINGS_FRACTION + ", "
                        + SettingsEntry.COLUMN_SETTINGS_CALORIES + ", "
                        + SettingsEntry.COLUMN_SETTINGS_PROTEINS + ", "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1 + ", "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2 + ", "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3 + ", "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4 + ", "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5 + ", "
                        + SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6 + ""
                        + " FROM tmp_settings;";

                db.execSQL(SQL_FILL_SETTINGS_TABLE);

                String SQL_DROP_TMP_SETTINGS_TABLE =  "DROP TABLE tmp_settings;";

                db.execSQL(SQL_DROP_TMP_SETTINGS_TABLE);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}