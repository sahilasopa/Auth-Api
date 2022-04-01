package com.sahilasopa.authentication.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository UserRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = UserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public void addNewUser(User user) {
        if (user != null) {
            if (userRepository.userExistsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Email is already registered");
            } else if (user.getUsername().isEmpty() || user.getUsername() == null) {
                throw new IllegalArgumentException("Username is required");
            } else if (userRepository.userExistsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("Username is already registered");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("user");
            userRepository.save(user);
        }
    }

    public void deleteUser(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalStateException("Invalid id");
        }
        userRepository.deleteById(id);
    }

    public void updateUser(long UserId, String name, String email) {
        Optional<User> userOptional = userRepository.findById(UserId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!Objects.equals(name, user.getUsername())) {
                if (isUsernameOrEmailValid(name)) {
                    user.setUsername(name);
                } else
                    throw new IllegalArgumentException("Username is already registered");
            }
            if (!Objects.equals(email, user.getEmail())) {
                if (isUsernameOrEmailValid(email)) {
                    user.setEmail(email);
                } else
                    throw new IllegalArgumentException("Email is already registered");
            }
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User does not exists");
        }
    }

    public boolean isUsernameOrEmailValid(String data) {
        if (data == null || data.isEmpty() || data.isBlank()) {
            return false;
        }
        return !(userRepository.userExistsByUsername(data) || userRepository.userExistsByEmail(data));
    }
}
