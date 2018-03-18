package ru.profitcode.ketocalc.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Renat on 18.03.2018.
 * Класс для представления данных об ингридиенте рецепта
 */

public final class ReceiptIngredientDto implements Parcelable {

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

    public ReceiptIngredientDto() {
        this.uid = UUID.randomUUID();
    }

    private ReceiptIngredientDto(Parcel in) {
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

    public static final Parcelable.Creator<ReceiptIngredientDto> CREATOR
            = new Parcelable.Creator<ReceiptIngredientDto>() {
        public ReceiptIngredientDto createFromParcel(Parcel in) {
            return new ReceiptIngredientDto(in);
        }

        public ReceiptIngredientDto[] newArray(int size) {
            return new ReceiptIngredientDto[size];
        }
    };
}
