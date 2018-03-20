package ru.profitcode.ketocalc.services;

import ru.profitcode.ketocalc.models.RecommendedBzu;

/**
 * Created by Renat on 20.03.2018.
 */

public final class BzuCalculatorService {
    public static RecommendedBzu getRecommendedBzu(Double calories, Double fraction, Double protein, Double portion)
    {
        if(portion == 0)
        {
            return new RecommendedBzu(0.0, 0.0, 0.0);
        }

        return new RecommendedBzu(1.0, 2.0, 3.0);
    }
}
