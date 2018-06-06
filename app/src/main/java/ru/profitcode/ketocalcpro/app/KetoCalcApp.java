package ru.profitcode.ketocalcpro.app;

import android.app.Application;

import ru.profitcode.ketocalcpro.singletones.CurrentSettingsSingleton;
import ru.profitcode.ketocalcpro.singletones.ProductSelectorSingleton;
import ru.profitcode.ketocalcpro.utils.Log;

public class KetoCalcApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("KetoCalcApp", "onCreate KetoCalcApp");

        CurrentSettingsSingleton.initInstance(getApplicationContext());
        ProductSelectorSingleton.initInstance();
    }
}
