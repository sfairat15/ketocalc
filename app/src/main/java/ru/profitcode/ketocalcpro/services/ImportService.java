package ru.profitcode.ketocalcpro.services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import ru.profitcode.ketocalcpro.data.KetoContract;
import ru.profitcode.ketocalcpro.utils.Log;

public final class ImportService {
    private static String LOG_TAG = "ImportService";
    static Uri SETTINGS_URI = Uri
            .parse("content://ru.profitcode.ketocalc.data/settings");

    public static void importFromFreeApplication(Context context) throws Exception
    {
        try {
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

            if (cursor.moveToFirst()) {
                int caloriesIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES);

                Log.d(LOG_TAG, String.valueOf(cursor.getDouble(caloriesIndex)));
            }

            cursor.close();



        } catch (Exception e) {
            Log.e(LOG_TAG, "backup error", e);
            throw e;
        }
    }
}
