package ru.profitcode.ketocalc.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import ru.profitcode.ketocalc.data.KetoContract.ProductEntry;
import ru.profitcode.ketocalc.data.KetoContract.SettingsEntry;

/**
 * {@link ContentProvider} for Keto app.
 */
public class KetoProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = KetoProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single product in the products table */
    private static final int PRODUCT_ID = 101;

    /** URI matcher code for the content URI for the settings table */
    private static final int SETTINGS = 200;

    /** URI matcher code for the content URI for a single setting in the settings table */
    private static final int SETTINGS_ID = 201;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://ru.profitcode.ketocalc.data/products" will map to the
        // integer code {@link #PRODUCTS}. This URI is used to provide access to MULTIPLE rows
        // of the products table.
        sUriMatcher.addURI(KetoContract.CONTENT_AUTHORITY, KetoContract.PATH_PRODUCTS, PRODUCTS);

        // The content URI of the form "content://ru.profitcode.ketocalc.data/products/#" will map to the
        // integer code {@link #PRODUCT_ID}. This URI is used to provide access to ONE single row
        // of the products table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://ru.profitcode.ketocalc.data/products/3" matches, but
        // "content://ru.profitcode.ketocalc.data/products" (without a number at the end) doesn't match.
        sUriMatcher.addURI(KetoContract.CONTENT_AUTHORITY, KetoContract.PATH_PRODUCTS + "/#", PRODUCT_ID);

        sUriMatcher.addURI(KetoContract.CONTENT_AUTHORITY, KetoContract.PATH_SETTINGS, SETTINGS);
        sUriMatcher.addURI(KetoContract.CONTENT_AUTHORITY, KetoContract.PATH_SETTINGS + "/#", SETTINGS_ID);
    }

    /** Database helper object */
    private KetoDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new KetoDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the products table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the products table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://ru.profitcode.ketocalc.data/products/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SETTINGS:
                cursor = database.query(SettingsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SETTINGS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(SettingsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            case SETTINGS:
                return insertSettings(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the tag is valid
        Integer tag = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_TAG);
        if (tag != null && !ProductEntry.isValidTag(tag)) {
            throw new IllegalArgumentException("Product requires valid tag");
        }

        // If the protein is provided, check that it's greater than or equal to 0 g
        Double protein = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PROTEIN);
        if (protein == null || protein < 0) {
            throw new IllegalArgumentException("Product requires valid protein");
        }

        // If the fat is provided, check that it's greater than or equal to 0 g
        Double fat = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_FAT);
        if (fat == null || fat < 0) {
            throw new IllegalArgumentException("Product requires valid fat");
        }
        
        // If the carbo is provided, check that it's greater than or equal to 0 g
        Double carbo = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_CARBO);
        if (carbo == null || carbo < 0) {
            throw new IllegalArgumentException("Product requires valid carbo");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertSettings(Uri uri, ContentValues values) {
        // Check that the name is not null
        Double fraction = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FRACTION);
        if (fraction == null || fraction < 0) {
            throw new IllegalArgumentException("Invalid fraction");
        }

        Double calories = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_CALORIES);
        if (calories == null || calories < 0) {
            throw new IllegalArgumentException("Invalid calories");
        }

        Double proteins = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_PROTEINS);
        if (proteins == null || proteins < 0) {
            throw new IllegalArgumentException("Invalid proteins");
        }

        Double foodPortions1 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1);
        if (foodPortions1 < 0) {
            throw new IllegalArgumentException("Invalid foodPortions1");
        }

        if(foodPortions1 == null)
        {
            foodPortions1 = 0.0;
        }

        Double foodPortions2 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2);
        if (foodPortions2 < 0) {
            throw new IllegalArgumentException("Invalid foodPortions2");
        }

        if(foodPortions2 == null)
        {
            foodPortions2 = 0.0;
        }

        Double foodPortions3 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3);
        if (foodPortions3 < 0) {
            throw new IllegalArgumentException("Invalid foodPortions3");
        }

        if(foodPortions3 == null)
        {
            foodPortions3 = 0.0;
        }

        Double foodPortions4 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4);
        if (foodPortions4 < 0) {
            throw new IllegalArgumentException("Invalid foodPortions4");
        }

        if(foodPortions4 == null)
        {
            foodPortions4 = 0.0;
        }

        Double foodPortions5 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5);
        if (foodPortions5 < 0) {
            throw new IllegalArgumentException("Invalid foodPortions5");
        }

        if(foodPortions5 == null)
        {
            foodPortions5 = 0.0;
        }

        Double foodPortions6 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6);
        if (foodPortions6 < 0) {
            throw new IllegalArgumentException("Invalid foodPortions6");
        }

        if(foodPortions6 == null)
        {
            foodPortions6 = 0.0;
        }

        if (foodPortions1 == 0 && foodPortions2 == 0 && foodPortions3 == 0 &&
                foodPortions4 == 0 && foodPortions5 == 0 && foodPortions6 == 0) {
            throw new IllegalArgumentException("At least one foodPortion should have a value");
        }

        if ((foodPortions1 + foodPortions2 + foodPortions3 +
                foodPortions4 + foodPortions5 + foodPortions6) > 100) {
            throw new IllegalArgumentException("Sum of foodPortions should be less than 100");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(SettingsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case SETTINGS_ID:
                selection = SettingsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateSettings(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateSettings(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_FRACTION)) {
            Double fraction = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FRACTION);
            if (fraction == null || fraction < 0) {
                throw new IllegalArgumentException("Settings require valid fraction");
            }
        }

        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_CALORIES)) {
            Double calories = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_CALORIES);
            if (calories == null || calories < 0) {
                throw new IllegalArgumentException("Settings require valid calories");
            }
        }

        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_PROTEINS)) {
            Double proteins = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_PROTEINS);
            if (proteins == null || proteins < 0) {
                throw new IllegalArgumentException("Settings require valid proteins");
            }
        }

        Double foodPortion1 = 0.0;
        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1)) {
            foodPortion1 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1);
            if (foodPortion1 == null || foodPortion1 < 0) {
                throw new IllegalArgumentException("Settings require valid food portion 1");
            }
        }

        Double foodPortion2 = 0.0;
        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2)) {
            foodPortion2 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2);
            if (foodPortion2 == null || foodPortion2 < 0) {
                throw new IllegalArgumentException("Settings require valid food portion 2");
            }
        }

        Double foodPortion3 = 0.0;
        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3)) {
            foodPortion3 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3);
            if (foodPortion3 == null || foodPortion3 < 0) {
                throw new IllegalArgumentException("Settings require valid food portion 3");
            }
        }

        Double foodPortion4 = 0.0;
        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4)) {
            foodPortion4 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4);
            if (foodPortion4 == null || foodPortion4 < 0) {
                throw new IllegalArgumentException("Settings require valid food portion 4");
            }
        }

        Double foodPortion5 = 0.0;
        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5)) {
            foodPortion5 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5);
            if (foodPortion5 == null || foodPortion5 < 0) {
                throw new IllegalArgumentException("Settings require valid food portion 5");
            }
        }

        Double foodPortion6 = 0.0;
        if (values.containsKey(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6)) {
            foodPortion6 = values.getAsDouble(SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6);
            if (foodPortion6 == null || foodPortion6 < 0) {
                throw new IllegalArgumentException("Settings require valid food portion 6");
            }
        }

        if (foodPortion1 == 0 && foodPortion2 == 0 && foodPortion3 == 0 &&
                foodPortion4 == 0 && foodPortion5 == 0 && foodPortion6 == 0) {
            throw new IllegalArgumentException("At least one foodPortion should have a value");
        }

        if ((foodPortion1 + foodPortion2 + foodPortion3 +
                foodPortion4 + foodPortion5 + foodPortion6) > 100) {
            throw new IllegalArgumentException("Sum of foodPortions should be less than 100");
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update products in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ProductEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_TAG} key is present,
        // check that the gender value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_TAG)) {
            Integer tag = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_TAG);
            if (tag != null && !ProductEntry.isValidTag(tag)) {
                throw new IllegalArgumentException("Product requires valid tag");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_PROTEIN} key is present,
        // check that the protein value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PROTEIN)) {
            // Check that the protein is greater than or equal to 0 kg
            Double protein = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PROTEIN);
            if (protein == null || protein < 0) {
                throw new IllegalArgumentException("Product requires valid protein");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_FAT} key is present,
        // check that the fat value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_FAT)) {
            // Check that the fat is greater than or equal to 0 kg
            Double fat = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_FAT);
            if (fat == null || fat < 0) {
                throw new IllegalArgumentException("Product requires valid fat");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_CARBO} key is present,
        // check that the carbo value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_CARBO)) {
            // Check that the carbo is greater than or equal to 0 kg
            Double carbo = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_CARBO);
            if (carbo == null || carbo < 0) {
                throw new IllegalArgumentException("Product requires valid carbo");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case SETTINGS:
                return SettingsEntry.CONTENT_LIST_TYPE;
            case SETTINGS_ID:
                return SettingsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
