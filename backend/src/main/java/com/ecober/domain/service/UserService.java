package com.ecober.domain.service;

import com.ecober.adapter.Dto.CarbonDTO;
import com.ecober.adapter.Dto.UserDTO;
import com.ecober.adapter.Dto.UserProfileDTO;
import com.ecober.adapter.mapper.UserMapper;
import com.ecober.domain.model.Trip;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.infrastructure.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    Co2AnalyticsService co2AnalyticsService;

    public String login(String username, String password, HttpSession session) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return "Invalid username";
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid password";
        }

        session.setAttribute("riderId", user.getUserId());
        return "SUCCESS";
    }
    public UserProfileDTO buildUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CarbonDTO carbonStats = co2AnalyticsService.getRiderCarbonEmission(userId);

        return UserProfileDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .tripCount(carbonStats.getTotalTrips())
                .totalCO2Saved(carbonStats.getTotalEmissions())
                .averageCO2Saved(carbonStats.getAverageEmissionPerTrip())
                .ecoBadge(carbonStats.getEcoBadge())
                .build();
    }

}
