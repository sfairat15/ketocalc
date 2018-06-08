package ru.profitcode.ketocalcpropaid.app;

import android.app.Application;

import ru.profitcode.ketocalcpropaid.singletones.CurrentSettingsSingleton;
import ru.profitcode.ketocalcpropaid.singletones.ProductSelectorSingleton;
import ru.profitcode.ketocalcpropaid.utils.Log;

public class KetoCalcApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("KetoCalcApp", "onCreate KetoCalcApp");

        CurrentSettingsSingleton.initInstance(getApplicationContext());
        ProductSelectorSingleton.initInstance();
    }
}
