package com.sahilasopa.chat.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
            if (userRepository.findUserByEmail(user.getEmail()) != null) {
                throw new IllegalArgumentException("Email is already registered");
            } else if (user.getUsername().isEmpty() || user.getUsername() == null) {
                throw new IllegalArgumentException("Username is required");
            } else if (userRepository.findUserByUsername(user.getUsername()) != null) {
                throw new IllegalArgumentException("Username is already registered");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("user");
            userRepository.save(user);
        }
    }

    public void deleteUser(long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        }
        throw new IllegalStateException("Invalid id");
    }

    public void updateUser(long UserId, String name, String email) {
        Optional<User> User = userRepository.findById(UserId);
        if (User.isPresent()) {
            if (name != null && !name.isEmpty()) {
                User.get().setUsername(name);
            }
            if (email != null && !email.isEmpty()) {
                if (userRepository.findUserByEmail(email) != null) {
                    throw new IllegalStateException("Invalid email");
                }
                User.get().setEmail(email);
            }
        }
    }
}
