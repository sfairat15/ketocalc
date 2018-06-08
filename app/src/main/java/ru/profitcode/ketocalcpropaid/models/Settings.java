package ru.profitcode.ketocalcpropaid.models;

import java.io.Serializable;

public class Settings implements Serializable {
    private Double calories = 0.0;
    private Double fraction = 0.0;
    private Double proteins = 0.0;

    private Double portion1 = 0.0;
    private Double portion2 = 0.0;
    private Double portion3 = 0.0;
    private Double portion4 = 0.0;
    private Double portion5 = 0.0;
    private Double portion6 = 0.0;

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

    public Double getPortion1() {
        return portion1;
    }

    public void setPortion1(Double portion1) {
        this.portion1 = portion1;
    }

    public Double getPortion2() {
        return portion2;
    }

    public void setPortion2(Double portion2) {
        this.portion2 = portion2;
    }

    public Double getPortion3() {
        return portion3;
    }

    public void setPortion3(Double portion3) {
        this.portion3 = portion3;
    }

    public Double getPortion4() {
        return portion4;
    }

    public void setPortion4(Double portion4) {
        this.portion4 = portion4;
    }

    public Double getPortion5() {
        return portion5;
    }

    public void setPortion5(Double portion5) {
        this.portion5 = portion5;
    }

    public Double getPortion6() { return portion6; }

    public void setPortion6(Double portion6) {
        this.portion6 = portion6;
    }

    public Integer getPortionCount()
    {
        Integer portionCount = 0;

        if(getPortion1() > 0)
        {
            portionCount++;
        }

        if(getPortion2() > 0)
        {
            portionCount++;
        }

        if(getPortion3() > 0)
        {
            portionCount++;
        }

        if(getPortion4() > 0)
        {
            portionCount++;
        }

        if(getPortion5() > 0)
        {
            portionCount++;
        }

        if(getPortion6() > 0)
        {
            portionCount++;
        }

        return portionCount;
    }
}
