package com.ecober.security;

import com.ecober.domain.model.User;
import com.ecober.domain.model.Driver;
import com.ecober.infrastructure.repository.UserRepository;
import com.ecober.infrastructure.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try USER first
        return userRepository.findByUsername(username)
            .<UserDetails>map(user -> new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), "USER"))
            // Then try DRIVER if not found
            .orElseGet(() -> driverRepository.findByEmail(username)
                .map(driver -> new CustomUserDetails(driver.getDriverId(), driver.getEmail(), driver.getPassword(), "DRIVER"))
                .orElseThrow(() -> new UsernameNotFoundException("No user or driver found for: " + username)));
    }
}
