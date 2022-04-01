package com.sahilasopa.authentication.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, passwordEncoder);
    }


    @Test
    void canGetAllUsers() {
        underTest.getUsers();
        verify(userRepository).findAll();
    }

    @Test
    void getUserByUsername() {
        User user = new User(
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        underTest.addNewUser(user);
        underTest.getUserByUsername("test");
        assertThat(user.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void canAddNewUser() {
        User user = new User(
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        underTest.addNewUser(user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void cantAddNewUserWithRegisteredEmail() {
        User user = new User(
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        given(userRepository.userExistsByEmail(user.getEmail()))
                .willReturn(true);
        assertThatThrownBy(() -> underTest.addNewUser(user))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cantAddNewUserWithRegisteredUsername() {
        User user = new User(
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        given(userRepository.userExistsByUsername(user.getUsername()))
                .willReturn(true);
        assertThatThrownBy(() -> underTest.addNewUser(user))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void cantAddNewUserWithBlankUsername() {
        User user = new User(
                "",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        assertThatThrownBy(() -> underTest.addNewUser(user))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void deleteUser() {
        User user = new User(
                1,
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        when(userRepository.findById((long) user.getId())).thenReturn(Optional.of(user));
        underTest.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById((long) user.getId());
    }

    @Test
    void deleteInvalidUser() {
        assertThatThrownBy(() -> underTest.deleteUser(0L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void canUpdateValidUser() {
        User user = new User(
                1,
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        when(userRepository.findById((long) user.getId())).thenReturn(Optional.of(user));
        underTest.updateUser(user.getId(), "test-updated", "test@test-updated.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void canUpdateInvalidUser() {
        User user = new User(
                1,
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        when(userRepository.findById((long) user.getId())).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> underTest.updateUser(user.getId(), "", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}