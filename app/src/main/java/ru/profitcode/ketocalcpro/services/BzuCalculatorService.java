package ru.profitcode.ketocalcpro.services;

import ru.profitcode.ketocalcpro.models.Bzu;
import ru.profitcode.ketocalcpro.utils.DoubleUtils;

public final class BzuCalculatorService {
    public static Bzu getRecommendedBzu(Double calories, Double fraction, Double protein,
                                        Double portion, Integer portionCount)
    {
        if(portion == 0 || portionCount == 0 || protein == 0)
        {
            return new Bzu(0.0, 0.0, 0.0);
        }

        Double caloriesPerBlock = 9*fraction + 4;
        Double itemsPerDay = calories/caloriesPerBlock;
        Double itemsPerPortion = itemsPerDay/portionCount;

        Double proteinPerDay = protein;
        Double fatPerDay = itemsPerPortion*fraction*portionCount;
        Double carboPerDay = itemsPerDay - protein;

        Double proteinPerPortion = DoubleUtils.roundOne(proteinPerDay*portion/100);
        Double fatPerPortion = DoubleUtils.roundOne(fatPerDay*portion/100);
        Double carboPerPortion = DoubleUtils.roundOne(carboPerDay*portion/100);

        return new Bzu(proteinPerPortion, fatPerPortion, carboPerPortion);
    }

    public static Bzu get100GrammBzu(Double totalWeight, Double protein, Double fat, Double carbo)
    {
        if(totalWeight == 0)
        {
            return new Bzu(0.0, 0.0, 0.0);
        }

        Double protein100 = DoubleUtils.roundOne((protein*100)/totalWeight);
        Double fat100 = DoubleUtils.roundOne((fat*100)/totalWeight);
        Double carbo100 = DoubleUtils.roundOne((carbo*100)/totalWeight);

        return new Bzu(protein100, fat100, carbo100);
    }
}
