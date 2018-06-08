package ru.profitcode.ketocalcpropaid.services;

import android.os.Environment;
import ru.profitcode.ketocalcpropaid.utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import ru.profitcode.ketocalcpropaid.data.KetoDbHelper;

public final class BackupService {
    private static String LOG_TAG = "BackupService";
    private static String BACKUP_FOLDER = "KetoCalcProPaid";
    private static String BACKUP_FILENAME = "KetoCalc.db";

    public static String getBackupDatabasePath() throws Exception
    {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (isExternalStorageWritable()) {
                String backupDBPath = BACKUP_FOLDER + "/"+BACKUP_FILENAME;
                File backupDB = new File(sd, backupDBPath);
                return backupDB.getPath();
            }
            else
            {
                throw new Exception("Do not have write access");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "backup error", e);
            throw e;
        }
    }

    public static void backupDatabase() throws Exception
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (isExternalStorageWritable()) {
                makeBackupFolder();

                String currentDBPath = "//data//ru.profitcode.ketocalcpropaid//databases//" + KetoDbHelper.DATABASE_NAME;
                String backupDBPath = BACKUP_FOLDER + "/"+BACKUP_FILENAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            else
            {
                throw new Exception("Do not have write access");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "backup error", e);
            throw e;
        }
    }

    private static void makeBackupFolder() throws Exception {
        File file = new File(Environment.getExternalStorageDirectory() +"/" + BACKUP_FOLDER);

        if (!file.exists())
        {
            if(!file.mkdirs())
            {
                throw new Exception("Could not create folder " + file.getPath());
            }
        }
    }

    public static void restoreDatabase() throws Exception
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (isExternalStorageReadable()) {
                String currentDBPath = "//data//ru.profitcode.ketocalcpropaid//databases//" + KetoDbHelper.DATABASE_NAME;
                String backupDBPath = BACKUP_FOLDER + "/"+BACKUP_FILENAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            else
            {
                throw new Exception("Do not have read access");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "restore error", e);
            throw e;
        }
    }


    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
