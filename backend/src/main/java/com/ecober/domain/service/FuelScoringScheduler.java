package com.ecober.domain.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ecober.domain.model.Driver;
import com.ecober.infrastructure.repository.DriverRepository;

@Service
public class FuelScoringScheduler {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private FuelScoringService fuelScoringService;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void updateAllDriverScores() {
        List<Driver> drivers = driverRepository.findAll();
        for (Driver driver : drivers) {
            if (driver.getVin() != null && !driver.getVin().isBlank()) {
                double ecoScore = fuelScoringService.computeEcoScoreFromVin(driver.getVin());
                driver.setTrustScore(ecoScore);//As Ecober places greater emphasis on eco-friendliness, we consider the ecoScore as the trustScore for now. However, trustScore typically encompasses multiple dimensions.
                driverRepository.save(driver);
            }
        }
        System.out.println("EcoScores updated for all drivers");
    }
}
