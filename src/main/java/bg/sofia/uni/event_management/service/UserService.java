package bg.sofia.uni.event_management.service;

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
    public void updateUser(long id, UserRequest req) {
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

       // userRepository.save(user);
    }

    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User is not found!");
        }

        userRepository.deleteById(id);
    }
}
