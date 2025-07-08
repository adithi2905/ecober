package com.ecober.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.util.FuelMappingUtil;

@Service
public class FuelScoringService {

    @Autowired
    FuelMappingUtil fuelMappingUtil;

    public double getFuelScore(String fuelType)
    {
        if (fuelType == null) 
            return 20.0; // default low score

        switch (fuelType.toLowerCase()) {
            case "electric":
                return 100.0;
            case "hybrid":
            case "plug-in hybrid":
                return 80.0;
            case "diesel":
                return 40.0;
            case "gasoline":
            default:
                return 30.0;
        }
    }

    public double computeEcoScoreFromVin(String vin) {
         String fuelType=fuelMappingUtil.getFuelTypeByVin(vin);
        return getFuelScore(fuelType);
    }
    
}
