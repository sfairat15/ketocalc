package ru.profitcode.ketocalc.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * API Contract for the Products app.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "ru.profitcode.ketocalc.data";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://ru.profitcode.ketocalc.data/products/ is a valid path for
     * looking at product data. content://ru.profitcode.ketocalc.data/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCTS = "products";

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
         * Returns whether or not the given gender is {@link #TAG_UNKNOWN}, {@link #TAG_HIGHFAT},
         * or {@link #TAG_HIGHCARBO}, or {@link #TAG_HIGHPROTEIN}.
         */
        public static boolean isValidTag(int gender) {
            if (gender == TAG_UNKNOWN
                    || gender == TAG_HIGHFAT
                    || gender == TAG_HIGHCARBO
                    || gender == TAG_HIGHPROTEIN) {
                return true;
            }
            return false;
        }
    }

}

