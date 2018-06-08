package ru.profitcode.ketocalcpropaid.services;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import ru.profitcode.ketocalcpropaid.R;
import ru.profitcode.ketocalcpropaid.data.KetoContract;
import ru.profitcode.ketocalcpropaid.singletones.CurrentSettingsSingleton;
import ru.profitcode.ketocalcpropaid.utils.Log;

public final class ImportService {
    private static String LOG_TAG = "ImportService";
    static Uri SETTINGS_URI = Uri.parse("content://ru.profitcode.ketocalc.data/settings");
    static Uri PRODUCTS_URI = Uri.parse("content://ru.profitcode.ketocalc.data/products");
    static Uri DISHES_URI = Uri.parse("content://ru.profitcode.ketocalc.data/dishes");
    static Uri RECEIPTS_URI = Uri.parse("content://ru.profitcode.ketocalc.data/receipts");

    public static void importFromFreeApplication(Context context) throws Exception
    {
        try {
            importSettings(context);
            importProducts(context);
            importDishes(context);
            importReceipts(context);
        } catch (Exception e) {
            Log.e(LOG_TAG, "import error", e);
            throw e;
        }
    }

    private static void importReceipts(Context context) {
        String[] projection = {
                KetoContract.ReceiptEntry._ID,
                KetoContract.ReceiptEntry.COLUMN_RECEIPT_NAME,
                KetoContract.ReceiptEntry.COLUMN_RECEIPT_MEAL,
                KetoContract.ReceiptEntry.COLUMN_RECEIPT_NOTE,
                KetoContract.ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS };

        Cursor cursor = context.getContentResolver().query(RECEIPTS_URI, projection, null,
                null, null);

        if (cursor == null) {
            return;
        }

        if(cursor.getCount() < 1)
        {
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(KetoContract.ReceiptEntry.COLUMN_RECEIPT_NAME);
            int noteColumnIndex = cursor.getColumnIndex(KetoContract.ReceiptEntry.COLUMN_RECEIPT_NOTE);
            int mealColumnIndex = cursor.getColumnIndex(KetoContract.ReceiptEntry.COLUMN_RECEIPT_MEAL);
            int ingredientsColumnIndex = cursor.getColumnIndex(KetoContract.ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int meal = cursor.getInt(mealColumnIndex);
            String note = cursor.getString(noteColumnIndex);
            String ingredientsJson = cursor.getString(ingredientsColumnIndex);

            ContentValues values = new ContentValues();
            values.put(KetoContract.ReceiptEntry.COLUMN_RECEIPT_NAME, name);
            values.put(KetoContract.ReceiptEntry.COLUMN_RECEIPT_NOTE, note);
            values.put(KetoContract.ReceiptEntry.COLUMN_RECEIPT_MEAL, meal);
            values.put(KetoContract.ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS, ingredientsJson);

            try {
                Uri newUri = context.getContentResolver().insert(KetoContract.ReceiptEntry.CONTENT_URI, values);
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, "import error", e);
                throw e;
            }
        }

        cursor.close();
    }

    private static void importDishes(Context context) {
        String[] projection = {
                KetoContract.DishEntry._ID,
                KetoContract.DishEntry.COLUMN_DISH_NAME,
                KetoContract.DishEntry.COLUMN_DISH_NOTE,
                KetoContract.DishEntry.COLUMN_DISH_INGREDIENTS };

        Cursor cursor = context.getContentResolver().query(DISHES_URI, projection, null,
                null, null);

        if (cursor == null) {
            return;
        }

        if(cursor.getCount() < 1)
        {
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(KetoContract.DishEntry.COLUMN_DISH_NAME);
            int noteColumnIndex = cursor.getColumnIndex(KetoContract.DishEntry.COLUMN_DISH_NOTE);
            int ingredientsColumnIndex = cursor.getColumnIndex(KetoContract.DishEntry.COLUMN_DISH_INGREDIENTS);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String note = cursor.getString(noteColumnIndex);
            String ingredientsJson = cursor.getString(ingredientsColumnIndex);

            ContentValues values = new ContentValues();
            values.put(KetoContract.DishEntry.COLUMN_DISH_NAME, name);
            values.put(KetoContract.DishEntry.COLUMN_DISH_NOTE, note);
            values.put(KetoContract.DishEntry.COLUMN_DISH_INGREDIENTS, ingredientsJson);

            try {
                Uri newUri = context.getContentResolver().insert(KetoContract.DishEntry.CONTENT_URI, values);
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, "import error", e);
                throw e;
            }
        }

        cursor.close();
    }

    private static void importProducts(Context context) {
        String[] projection = {
                KetoContract.ProductEntry._ID,
                KetoContract.ProductEntry.COLUMN_PRODUCT_NAME,
                KetoContract.ProductEntry.COLUMN_PRODUCT_PROTEIN,
                KetoContract.ProductEntry.COLUMN_PRODUCT_FAT,
                KetoContract.ProductEntry.COLUMN_PRODUCT_CARBO,
                KetoContract.ProductEntry.COLUMN_PRODUCT_TAG };

        Cursor cursor = context.getContentResolver().query(PRODUCTS_URI, projection, null,
                null, null);

        if (cursor == null) {
            return;
        }

        if(cursor.getCount() < 1)
        {
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int proteinColumnIndex = cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_PROTEIN);
            int fatColumnIndex = cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_FAT);
            int carboColumnIndex = cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_CARBO);
            int tagColumnIndex = cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_TAG);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            Double protein = cursor.getDouble(proteinColumnIndex);
            Double fat = cursor.getDouble(fatColumnIndex);
            Double carbo = cursor.getDouble(carboColumnIndex);
            int tag = cursor.getInt(tagColumnIndex);

            ContentValues values = new ContentValues();
            values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
            values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_TAG, tag);
            values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_PROTEIN, protein);
            values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_FAT, fat);
            values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_CARBO, carbo);

            try {
                Uri newUri = context.getContentResolver().insert(KetoContract.ProductEntry.CONTENT_URI, values);
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, "import error", e);
                throw e;
            }
        }

        cursor.close();
    }

    private static void importSettings(Context context) {
        String[] projection = {
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6 };

        Cursor cursor = context.getContentResolver().query(SETTINGS_URI, projection, null,
                null, null);

        if (cursor == null) {
            return;
        }

        if(cursor.getCount() < 1)
        {
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            // Find the columns of settings attributes that we're interested in
            int fractionColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION);
            int proteinsColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS);
            int caloriesColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES);
            int foodPortions1ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1);
            int foodPortions2ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2);
            int foodPortions3ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3);
            int foodPortions4ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4);
            int foodPortions5ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5);
            int foodPortions6ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6);

            // Extract out the value from the Cursor for the given column index
            Double fraction = cursor.getDouble(fractionColumnIndex);
            Double proteins = cursor.getDouble(proteinsColumnIndex);
            Double calories = cursor.getDouble(caloriesColumnIndex);
            Double foodPortions1 = cursor.getDouble(foodPortions1ColumnIndex);
            Double foodPortions2 = cursor.getDouble(foodPortions2ColumnIndex);
            Double foodPortions3 = cursor.getDouble(foodPortions3ColumnIndex);
            Double foodPortions4 = cursor.getDouble(foodPortions4ColumnIndex);
            Double foodPortions5 = cursor.getDouble(foodPortions5ColumnIndex);
            Double foodPortions6 = cursor.getDouble(foodPortions6ColumnIndex);

            // Create a ContentValues object where column names are the keys,
            // and settings attributes from the editor are the values.
            ContentValues values = new ContentValues();

            String name = context.getResources().getString(R.string.default_settings_name);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_NAME, name);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT, 1);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION, fraction);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS, proteins);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES, calories);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1, foodPortions1);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2, foodPortions2);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3, foodPortions3);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4, foodPortions4);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5, foodPortions5);
            values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6, foodPortions6);

            try {
                Uri newUri = context.getContentResolver().insert(KetoContract.SettingsEntry.CONTENT_URI, values);

                try {
                    long id = ContentUris.parseId(newUri);
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT, 0);
                    String where = KetoContract.SettingsEntry._ID + " <> ?";

                    //uncheck another settings with default value
                    context.getContentResolver().update( Uri.withAppendedPath(KetoContract.SettingsEntry.CONTENT_URI, "unset-is-default"), updateValues,
                            where, new String[] { Long.toString(id) });
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, "import error", e);
                   throw e;
                }
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, "import error", e);
                throw e;
            }
        }

        cursor.close();

        CurrentSettingsSingleton currentSettingsSingleton = CurrentSettingsSingleton.getInstance();
        currentSettingsSingleton.reloadSettings(context);
    }
}
