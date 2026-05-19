package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.UserRequest;
import bg.sofia.uni.event_management.dto.UserResponse;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.Role;
import bg.sofia.uni.event_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("Testov");
        user.setRole(Role.USER);
    }

    // GetAllUsers() Method

    @Test
    void testGetAllUsersReturnsAllUsersRightSize() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> users = userService.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    void testGetAllUsersReturnsAllUsersRightEmail() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> users = userService.getAllUsers();

        assertEquals("test@test.com", users.getFirst().email());
    }

    @Test
    void testGetAllUsersReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.getAllUsers()).isEmpty();
    }

    // getUserById() Method

    @Test
    void testGetUserByIdReturnsCorrectUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(this.user));

        assertEquals(UserResponse.from(user), userService.getUserById(1L));
    }

    @Test
    void testGetUserByIdThrowsNotFoundException() {
        when(userRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(100L));
    }

    // updateUser() Method

    @Test
    void testUpdateUserThrowsNotFoundException() {
        when(userRepository.findById(any())).thenThrow(NotFoundException.class);

        UserRequest req = new UserRequest("test@test.com", "Test", "Testov", "admin");

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(1L, req));
    }

    @Test
    void testUpdateUserNotValidRoleThrowsIllegalArgumentException() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserRequest req = new UserRequest("test@test.com",
                "Test", "Testov", "NotValidRole");

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, req));
    }

    @Test
    void testUpdateUserUpdateUserEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserRequest req = new UserRequest("testUpdate@test.com",
                "Test", "Testov", "User");

        userService.updateUser(1L, req);

        assertEquals("testUpdate@test.com", user.getEmail());
    }

    //  deleteUser() Method

    @Test
    void deleteUserExistingIdDeletesUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);

    }

    @Test
    void testDeleteUserMissingIdThrowsNotFoundException() {
        when(userRepository.existsById(any())).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> userService.deleteUser(1L));
    }
}
