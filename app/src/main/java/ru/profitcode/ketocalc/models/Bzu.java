package ru.profitcode.ketocalc.models;

public class Bzu {
    private Double protein;
    private Double fat;
    private Double carbo;


    public Bzu(Double protein, Double fat, Double carbo) {
        this.protein = protein;
        this.fat = fat;
        this.carbo = carbo;
    }

    public Double getProtein() {
        return protein;
    }

    public Double getFat() { return fat; }

    public Double getCarbo() {
        return carbo;
    }
}
