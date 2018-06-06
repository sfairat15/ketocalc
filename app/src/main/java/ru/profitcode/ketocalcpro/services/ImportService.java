package ru.profitcode.ketocalcpro.services;

import ru.profitcode.ketocalcpro.utils.Log;

public final class ImportService {
    private static String LOG_TAG = "ImportService";
    private static String BACKUP_FOLDER = "KetoCalc";
    private static String BACKUP_FILENAME = "KetoCalc.db";

    public static void importFromFreeApplication() throws Exception
    {
        try {
           ;
        } catch (Exception e) {
            Log.e(LOG_TAG, "backup error", e);
            throw e;
        }
    }
}
