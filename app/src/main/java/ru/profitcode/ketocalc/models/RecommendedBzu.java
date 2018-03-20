package ru.profitcode.ketocalc.models;

/**
 * Created by Renat on 20.03.2018.
 */

public class RecommendedBzu {
    private Double protein;
    private Double fat;
    private Double carbo;


    public RecommendedBzu(Double protein, Double fat, Double carbo) {
        this.protein = protein;
        this.fat = fat;
        this.carbo = carbo;
    }

    public Double getProtein() {
        return protein;
    }

    public Double getFat() {
        return fat;
    }

    public Double getCarbo() {
        return carbo;
    }
}
