package ru.profitcode.ketocalc.services;

import ru.profitcode.ketocalc.models.RecommendedBzu;

/**
 * Created by Renat on 20.03.2018.
 */

public final class BzuCalculatorService {
    public static RecommendedBzu getRecommendedBzu(Double calories, Double fraction, Double protein,
                                                   Double portion, Integer portionCount)
    {
        if(portion == 0 || portionCount == 0 || protein == 0)
        {
            return new RecommendedBzu(0.0, 0.0, 0.0);
        }

        Double caloriesPerBlock = 9*fraction + 4;
        Double itemsPerDay = calories/caloriesPerBlock;
        Double itemsPerPortion = itemsPerDay/portionCount;

        Double proteinPerDay = protein;
        Double fatPerDay = itemsPerPortion*fraction*portionCount;
        Double carboPerDay = itemsPerDay - protein;

        Double proteinPerPortion = round(proteinPerDay*portion/100);
        Double fatPerPortion = round(fatPerDay*portion/100);
        Double carboPerPortion = round(carboPerDay*portion/100);

        return new RecommendedBzu(proteinPerPortion, fatPerPortion, carboPerPortion);
    }

    private static Double round(Double value)
    {
        return Math.round(value*10)/10.0;
    }
}
