package ru.profitcode.ketocalcpro.singletones;

import android.content.Context;
import android.database.Cursor;

import ru.profitcode.ketocalcpro.data.KetoContract;
import ru.profitcode.ketocalcpro.models.Settings;
import ru.profitcode.ketocalcpro.utils.Log;

public class CurrentSettingsSingleton {
    private static CurrentSettingsSingleton mInstance;
    private Settings mCurrentSettings;

    public static void initInstance(Context context) {
        Log.d("CurrentSettingsSingleton", "CurrentSettingsSingleton::InitInstance()");
        if (mInstance == null) {
            mInstance = new CurrentSettingsSingleton(context);
        }
    }

    public static CurrentSettingsSingleton getInstance() {
        Log.d("CurrentSettingsSingleton", "CurrentSettingsSingleton::getInstance()");
        return mInstance;
    }

    private CurrentSettingsSingleton(Context context) {
        Log.w("CurrentSettingsSingleton", "CurrentSettingsSingleton::MySingleton()");

        //should try to get from db
        reloadSettings(context);
    }

    /*
    * Can return null
     */
    public Settings get() {
        return mCurrentSettings;
    }

    public void reloadSettings(Context context) {
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

        String where = KetoContract.SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT + " = 1";

        // This loader will execute the ContentProvider's query method on a background thread
        Cursor cursor = context.getContentResolver().query(
                KetoContract.SettingsEntry.CONTENT_URI,    // Query the content URI for the current settings
                projection,             // Columns to include in the resulting Cursor
                where,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

        if (cursor == null) {
            return;
        }

        if(cursor.getCount() < 1)
        {
            cursor.close();
            return;
        }

        Settings settings = new Settings();

        if (cursor.moveToFirst()) {
            int caloriesIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES);
            settings.setCalories(cursor.getDouble(caloriesIndex));

            int fractionIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION);
            settings.setFraction(cursor.getDouble(fractionIndex));

            int proteinsIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS);
            settings.setProteins(cursor.getDouble(proteinsIndex));

            int portion1Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1);
            settings.setPortion1(cursor.getDouble(portion1Index));

            int portion2Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2);
            settings.setPortion2(cursor.getDouble(portion2Index));

            int portion3Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3);
            settings.setPortion3(cursor.getDouble(portion3Index));

            int portion4Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4);
            settings.setPortion4(cursor.getDouble(portion4Index));

            int portion5Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5);
            settings.setPortion5(cursor.getDouble(portion5Index));

            int portion6Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6);
            settings.setPortion6(cursor.getDouble(portion6Index));
        }

        cursor.close();

        mCurrentSettings = settings;
    }
}
