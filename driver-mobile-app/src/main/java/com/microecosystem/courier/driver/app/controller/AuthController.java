package com.microecosystem.courier.driver.app.controller;

import com.microecosystem.courier.driver.app.dto.DriverDto;
import com.microecosystem.courier.driver.app.dto.auth.JwtResponse;
import com.microecosystem.courier.driver.app.dto.auth.LoginRequest;
import com.microecosystem.courier.driver.app.dto.auth.SignupRequest;
import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import com.microecosystem.courier.driver.app.model.Role;
import com.microecosystem.courier.driver.app.model.RoleName;
import com.microecosystem.courier.driver.app.model.User;
import com.microecosystem.courier.driver.app.repository.RoleRepository;
import com.microecosystem.courier.driver.app.repository.UserRepository;
import com.microecosystem.courier.driver.app.security.UserDetailsImpl;
import com.microecosystem.courier.driver.app.security.jwt.JwtUtils;
import com.microecosystem.courier.driver.app.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DriverService driverService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("REST request for user login: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Update device token if provided
        if (loginRequest.getDeviceToken() != null && !loginRequest.getDeviceToken().isEmpty()) {
            driverService.getDriverByUserId(userDetails.getId())
                    .ifPresent(driver -> driverService.updateDeviceToken(driver.getId(), loginRequest.getDeviceToken()));
        }

        // Get driver ID if exists
        Long driverId = driverService.getDriverByUserId(userDetails.getId())
                .map(Driver::getId)
                .orElse(null);

        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .driverId(driverId)
                .build());
    }

    @PostMapping("/signup")
    @Operation(summary = "Signup", description = "Registers a new user and creates a driver profile")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("REST request for user registration: {}", signupRequest.getUsername());

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create new user account
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setEmailVerified(false);

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role driverRole = roleRepository.findByName(RoleName.ROLE_DRIVER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(driverRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "super_admin":
                        Role superAdminRole = roleRepository.findByName(RoleName.ROLE_SUPER_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(superAdminRole);
                        break;
                    default:
                        Role driverRole = roleRepository.findByName(RoleName.ROLE_DRIVER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(driverRole);
                }
            });
        }

        user.setRoles(roles);
        user = userRepository.save(user);

        // Create driver profile
        DriverDto driverDto = DriverDto.builder()
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .email(signupRequest.getEmail())
                .phoneNumber(signupRequest.getPhoneNumber())
                .profilePictureUrl(signupRequest.getProfilePictureUrl())
                .status(DriverStatus.OFFLINE)
                .vehicleType(signupRequest.getVehicleType())
                .vehicleLicensePlate(signupRequest.getVehicleLicensePlate())
                .deviceToken(signupRequest.getDeviceToken())
                .isActive(true)
                .isVerified(false)
                .userId(user.getId())
                .build();

        Driver driver = driverService.createDriver(driverDto);

        return ResponseEntity.ok("User registered successfully!");
    }
} 