package ru.profitcode.ketocalc.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Receipt Ingredient
 */

public final class ReceiptIngredient {

    private Double weight;

    private Long productId;
    private String productName;
    private Double productProtein;
    private Double productFat;
    private Double productCarbo;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getProductProtein() {
        return productProtein;
    }

    public void setProductProtein(Double productProtein) {
        this.productProtein = productProtein;
    }

    public Double getProductFat() {
        return productFat;
    }

    public void setProductFat(Double productFat) {
        this.productFat = productFat;
    }

    public Double getProductCarbo() {
        return productCarbo;
    }

    public void setProductCarbo(Double productCarbo) {
        this.productCarbo = productCarbo;
    }

    public Double getTotalProtein() {
        return this.getWeight()*(this.getProductProtein()/100);
    }

    public Double getTotalFat() {
        return this.getWeight()*(this.getProductFat()/100);
    }

    public Double getTotalCarbo() {
        return this.getWeight()*(this.getProductCarbo()/100);
    }
}
