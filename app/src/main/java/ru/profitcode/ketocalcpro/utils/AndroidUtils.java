package ru.profitcode.ketocalcpro.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class AndroidUtils {
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
