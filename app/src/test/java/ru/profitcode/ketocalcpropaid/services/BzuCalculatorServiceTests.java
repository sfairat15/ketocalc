package ru.profitcode.ketocalcpropaid.services;

import org.junit.Test;

import ru.profitcode.ketocalcpropaid.models.Bzu;

import static org.junit.Assert.assertEquals;

public class BzuCalculatorServiceTests {
    @Test
    public void getRecommendedBzu_zeroProtein_shouldReturnZero() throws Exception {
        //arrange
        Double calories = 1.0;
        Double fraction = 3.0;
        Double protein = 0.0;
        Double portion = 20.0;
        Integer portionCount = 1;

        //act
        Bzu bzu = BzuCalculatorService.getRecommendedBzu(calories, fraction, protein, portion, portionCount);

        //assert
        assertEquals((Double)0.0, bzu.getProtein());
        assertEquals((Double)0.0, bzu.getFat());
        assertEquals((Double)0.0, bzu.getCarbo());
    }

    @Test
    public void getRecommendedBzu_zeroPortionCount_shouldReturnZero() throws Exception {
        //arrange
        Double calories = 1.0;
        Double fraction = 3.0;
        Double protein = 16.0;
        Double portion = 20.0;
        Integer portionCount = 0;

        //act
        Bzu bzu = BzuCalculatorService.getRecommendedBzu(calories, fraction, protein, portion, portionCount);

        //assert
        assertEquals((Double)0.0, bzu.getProtein());
        assertEquals((Double)0.0, bzu.getFat());
        assertEquals((Double)0.0, bzu.getCarbo());
    }

    @Test
    public void getRecommendedBzu_zeroPortion_shouldReturnZero() throws Exception {
        //arrange
        Double calories = 1.0;
        Double fraction = 3.0;
        Double protein = 16.0;
        Double portion = 0.0;
        Integer portionCount = 1;

        //act
        Bzu bzu = BzuCalculatorService.getRecommendedBzu(calories, fraction, protein, portion, portionCount);

        //assert
        assertEquals((Double)0.0, bzu.getProtein());
        assertEquals((Double)0.0, bzu.getFat());
        assertEquals((Double)0.0, bzu.getCarbo());
    }

    @Test
    public void getRecommendedBzu_correctData_shouldReturnAsExpected() throws Exception {
        //arrange
        Double calories = 1067.0;
        Double fraction = 2.7;
        Double protein = 16.5;
        Double portion = 20.0;
        Integer portionCount = 5;

        //act
        Bzu bzu = BzuCalculatorService.getRecommendedBzu(calories, fraction, protein, portion, portionCount);

        //assert
        assertEquals((Double)3.3, bzu.getProtein());
        assertEquals((Double)20.4, bzu.getFat());
        assertEquals((Double)4.2, bzu.getCarbo());
    }

    @Test
    public void get100GrammBzu_zeroWeight_shouldReturnZeroBzu() throws Exception {
        //arrange
        Double totalWeight = 0.0;
        Double protein = 15.0;
        Double fat = 22.9;
        Double carbo = 111.3;

        //act
        Bzu bzu = BzuCalculatorService.get100GrammBzu(totalWeight, protein, fat, carbo);

        //assert
        assertEquals((Double)0.0, bzu.getProtein());
        assertEquals((Double)0.0, bzu.getFat());
        assertEquals((Double)0.0, bzu.getCarbo());
    }

    @Test
    public void get100GrammBzu_correctData_shouldReturnAsExpected() throws Exception {
        //arrange
        Double totalWeight = 1120.0;
        Double protein = 15.0;
        Double fat = 22.9;
        Double carbo = 111.3;

        //act
        Bzu bzu = BzuCalculatorService.get100GrammBzu(totalWeight, protein, fat, carbo);

        //assert
        assertEquals((Double)1.3, bzu.getProtein());
        assertEquals((Double)2.0, bzu.getFat());
        assertEquals((Double)9.9, bzu.getCarbo());
    }
}