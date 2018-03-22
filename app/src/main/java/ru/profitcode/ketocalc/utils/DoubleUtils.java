package ru.profitcode.ketocalc.utils;

/**
 * Created by Renat on 22.03.2018.
 */

public class DoubleUtils {

    public static Double roundOne(Double value)
    {
        return round(value, 1);
    }

    public static Double round(Double value, Integer precision)
    {
        return Math.round(value*10*precision)/(10.0*precision);
    }
}
