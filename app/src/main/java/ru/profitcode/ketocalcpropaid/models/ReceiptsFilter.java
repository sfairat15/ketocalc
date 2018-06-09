package ru.profitcode.ketocalcpropaid.models;

import java.io.Serializable;

public class ReceiptsFilter implements Serializable {

    Integer mSelectedMeal = 0;
    String mQuery = null;

    public void setSelectedMeal(Integer selectedMeal) {
        mSelectedMeal = selectedMeal;
    }

    public Integer getSelectedMeal()
    {
        return mSelectedMeal;
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    public String getQuery()
    {
        return mQuery;
    }
}
