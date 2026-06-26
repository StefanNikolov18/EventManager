package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.AuthenticationRequest;
import bg.sofia.uni.event_management.dto.AuthenticationResponse;
import bg.sofia.uni.event_management.dto.RegisterRequest;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.Role;
import bg.sofia.uni.event_management.repository.UserRepository;
import bg.sofia.uni.event_management.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.USER)
                .hashPassword(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getHashPassword())
                .roles(user.getRole().name())
                .build();

        var jwtToken = jwtUtils.generateToken(userDetails);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new NotFoundException("Invalid email or password!");
        }

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Invalid email or password!"));

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getHashPassword())
                .roles(user.getRole().name())
                .build();

        var jwtToken = jwtUtils.generateToken(userDetails);

        return new AuthenticationResponse(jwtToken);
    }
}