package ru.profitcode.ketocalc.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * API Contract for the Keto app.
 */
public final class KetoContract {

    private KetoContract() {}

    public static final String CONTENT_AUTHORITY = "ru.profitcode.ketocalc.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_RECEIPTS = "receipts";
    public static final String PATH_SETTINGS = "settings";

    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Name of database table for products */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * Protein of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PROTEIN = "protein";

        /**
         * Fat of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_FAT = "fat";

        /**
         * Carbohydrates of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_CARBO = "carbo";

        /**
         * Tag of the product.
         *
         * The only possible values are {@link #TAG_UNKNOWN}, {@link #TAG_HIGHFAT},
         * or {@link #TAG_HIGHCARBO}, or {@link #TAG_HIGHPROTEIN}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_TAG = "tag";


        /**
         * Possible values for the tag of the product.
         */
        public static final int TAG_UNKNOWN = 0;
        public static final int TAG_HIGHPROTEIN = 1;
        public static final int TAG_HIGHFAT = 2;
        public static final int TAG_HIGHCARBO = 3;

        /**
         * Returns whether or not the given tag is {@link #TAG_UNKNOWN}, {@link #TAG_HIGHFAT},
         * or {@link #TAG_HIGHCARBO}, or {@link #TAG_HIGHPROTEIN}.
         */
        public static boolean isValidTag(int tag) {
            return tag == TAG_UNKNOWN
                    || tag == TAG_HIGHFAT
                    || tag == TAG_HIGHCARBO
                    || tag == TAG_HIGHPROTEIN;
        }
    }

    /**
     * Inner class that defines constant values for the settings database table.
     * Each entry in the table represents a single settings bucket.
     */
    public static final class SettingsEntry implements BaseColumns {

        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SETTINGS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SETTINGS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SETTINGS;

        /** Name of database table for products */
        public final static String TABLE_NAME = "settings";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Fraction.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_FRACTION ="fraction";

        /**
         * Calories per day.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_CALORIES = "calories";

        /**
         * Proteins per day.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_PROTEINS = "proteins";

        /**
         * Portion 1.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_FOOD_PORTIONS_1 = "food_portions_1";

        /**
         * Portion 2.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_FOOD_PORTIONS_2 = "food_portions_2";

        /**
         * Portion 3.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_FOOD_PORTIONS_3 = "food_portions_3";

        /**
         * Portion 4.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_FOOD_PORTIONS_4 = "food_portions_4";

        /**
         * Portion 5.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_FOOD_PORTIONS_5 = "food_portions_5";

        /**
         * Portion 6.
         *
         * Type: REAL
         */
        public final static String COLUMN_SETTINGS_FOOD_PORTIONS_6 = "food_portions_6";

    }

    /**
     * Inner class that defines constant values for the receipts database table.
     * Each entry in the table represents a single receipt.
     */
    public static final class ReceiptEntry implements BaseColumns {

        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECEIPTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of receipts.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECEIPTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single receipt.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECEIPTS;

        /** Name of database table for receipts */
        public final static String TABLE_NAME = "receipts";

        /**
         * Unique ID number for the receipt (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the receipt.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RECEIPT_NAME ="name";

        /**
         * Meal of the receipt.
         *
         * The only possible values are {@link #MEAL_UNKNOWN}, or {@link #MEAL_BREAKFAST},
         * or {@link #MEAL_DINNER}, or {@link #MEAL_AFTERNOON_SNACK}, or {@link #MEAL_SUPPER}
         * , or {@link #MEAL_LATE_SUPPER}, or {@link #MEAL_NIGHT_SNACK}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_RECEIPT_MEAL = "meal";

        /**
         * Ingredients of the receipt.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RECEIPT_INGREDIENTS = "ingredients";

        /**
         * Note of the receipt.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RECEIPT_NOTE = "note";


        /**
         * Possible values for the meal of the receipt.
         */
        public static final int MEAL_UNKNOWN = 0;
        public static final int MEAL_BREAKFAST = 1;
        public static final int MEAL_DINNER = 2;
        public static final int MEAL_AFTERNOON_SNACK = 3;
        public static final int MEAL_SUPPER = 4;
        public static final int MEAL_LATE_SUPPER = 5;
        public static final int MEAL_NIGHT_SNACK = 6;

        /**
         * Returns whether or not the given tag is {@link #MEAL_UNKNOWN}, or {@link #MEAL_BREAKFAST},
         * or {@link #MEAL_DINNER}, or {@link #MEAL_AFTERNOON_SNACK}, or {@link #MEAL_SUPPER}
         * , or {@link #MEAL_LATE_SUPPER}, or {@link #MEAL_NIGHT_SNACK}.
         */
        public static boolean isValidMeal(int meal) {
            return meal == MEAL_UNKNOWN
                    || meal == MEAL_BREAKFAST
                    || meal == MEAL_DINNER
                    || meal == MEAL_AFTERNOON_SNACK
                    || meal == MEAL_SUPPER
                    || meal == MEAL_LATE_SUPPER
                    || meal == MEAL_NIGHT_SNACK;
        }
    }

}

