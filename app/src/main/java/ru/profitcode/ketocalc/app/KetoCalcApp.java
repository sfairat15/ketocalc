package ru.profitcode.ketocalc.app;

import android.app.Application;

import ru.profitcode.ketocalc.singletones.CurrentSettingsSingleton;
import ru.profitcode.ketocalc.singletones.ProductSelectorSingleton;
import ru.profitcode.ketocalc.utils.Log;

public class KetoCalcApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("KetoCalcApp", "onCreate KetoCalcApp");

        CurrentSettingsSingleton.initInstance(getApplicationContext());
        ProductSelectorSingleton.initInstance();
    }
}
