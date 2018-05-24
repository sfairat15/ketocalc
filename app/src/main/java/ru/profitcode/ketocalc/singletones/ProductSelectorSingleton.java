package ru.profitcode.ketocalc.singletones;

import java.util.ArrayList;

import ru.profitcode.ketocalc.utils.Log;

public class ProductSelectorSingleton {
    private static ProductSelectorSingleton mInstance;
    private ArrayList<String> selectedProductIds;

    public static void initInstance() {
        Log.d("ProductSelectorSingleton", "ProductSelectorSingleton::InitInstance()");
        if (mInstance == null) {
            mInstance = new ProductSelectorSingleton();
        }
    }

    public static ProductSelectorSingleton getInstance() {
        Log.d("ProductSelectorSingleton", "ProductSelectorSingleton::getInstance()");
        return mInstance;
    }

    private ProductSelectorSingleton() {
        Log.w("ProductSelectorSingleton", "ProductSelectorSingleton::MySingleton()");
        selectedProductIds = new ArrayList<String>();
    }

    public void ClearSelection() {
        selectedProductIds.clear();
    }

    public void Add(String id) {
        selectedProductIds.add(id);
    }

    public void Remove(String id) {
        if(selectedProductIds.contains(id))
        {
            selectedProductIds.remove(id);
        }
    }

    public String[] GetSelection() {
        String[] outputStrArr = new String[selectedProductIds.size()];

        for (int i = 0; i < selectedProductIds.size(); i++) {
            outputStrArr[i] = selectedProductIds.get(i);
        }

        return outputStrArr;
    }
}
