package ru.profitcode.ketocalc.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Dish Ingredient Dto
 */

public final class DishIngredientDto implements Parcelable {

    private UUID uid;
    private Long ingredientId;
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

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
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

    public UUID getUid() {
        return this.uid;
    }

    public DishIngredientDto() {
        this.uid = UUID.randomUUID();
    }

    private DishIngredientDto(Parcel in) {
        uid = UUID.fromString(in.readString());

        ingredientId = in.readLong();
        weight = in.readDouble();

        productId = in.readLong();
        productName = in.readString();
        productProtein = in.readDouble();
        productFat = in.readDouble();
        productCarbo = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid.toString());

        dest.writeLong(ingredientId);
        dest.writeDouble(weight);

        dest.writeLong(productId);
        dest.writeString(productName);
        dest.writeDouble(productProtein);
        dest.writeDouble(productFat);
        dest.writeDouble(productCarbo);
    }

    public static final Creator<DishIngredientDto> CREATOR
            = new Creator<DishIngredientDto>() {
        public DishIngredientDto createFromParcel(Parcel in) {
            return new DishIngredientDto(in);
        }

        public DishIngredientDto[] newArray(int size) {
            return new DishIngredientDto[size];
        }
    };

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
