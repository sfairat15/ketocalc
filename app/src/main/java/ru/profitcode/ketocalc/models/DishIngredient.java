package ru.profitcode.ketocalc.models;

/**
 * Dish Ingredient
 */

public final class DishIngredient {

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
}
