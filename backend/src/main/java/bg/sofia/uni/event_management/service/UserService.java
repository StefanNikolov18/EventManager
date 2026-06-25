package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.AdminRequest;
import bg.sofia.uni.event_management.dto.UserRequest;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.Role;
import bg.sofia.uni.event_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =================== ADMIN ===========================

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User is missing with id: " + id));

        return UserResponse.from(user);
    }

    @Transactional
    public void updateUser(long id, AdminRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User is missing with id: " + id));

        Role roleEnum;
        try {
            roleEnum = Role.parseRole(req.role());
        } catch(IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + req.role(), ex);
        }

        user.setEmail(req.email());
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setRole(roleEnum);
    }

    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User is not found!");
        }

        userRepository.deleteById(id);
    }

    // ===================== CURRENT USER (JWT) =====================

    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not found with email: " + email));

        return UserResponse.from(user);
    }

    @Transactional
    public void updateByEmail(String email, UserRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not found with email: " + email));

        user.setEmail(req.email());
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());

    }

    public void deleteByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not found with email: " + email));

        userRepository.delete(user);
    }

}
