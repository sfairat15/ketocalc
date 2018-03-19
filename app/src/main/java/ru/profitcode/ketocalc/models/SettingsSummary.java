package ru.profitcode.ketocalc.models;

import java.io.Serializable;

/**
 * Created by Renat on 19.03.2018.
 */

public class SettingsSummary implements Serializable {
    private Double calories;
    private Double fraction;
    private Double proteins;

    public SettingsSummary(Double calories, Double fraction, Double proteins)
    {
        this.calories = calories;
        this.fraction = fraction;
        this.proteins = proteins;
    }


    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getFraction() {
        return fraction;
    }

    public void setFraction(Double fraction) {
        this.fraction = fraction;
    }

    public Double getProteins() {
        return proteins;
    }

    public void setProteins(Double proteins) {
        this.proteins = proteins;
    }
}
