package ru.profitcode.ketocalcpropaid.utils;

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