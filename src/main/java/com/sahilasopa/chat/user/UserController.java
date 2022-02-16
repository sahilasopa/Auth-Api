package com.sahilasopa.chat.user;

import com.sahilasopa.chat.auth.AuthenticationRequest;
import com.sahilasopa.chat.auth.AuthenticationResponse;
import com.sahilasopa.chat.auth.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping()
    public List<User> getUser() {
        return userService.getUsers();
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getPassword() != null) {
            userService.addNewUser(user);
            jwtUtil.generateToken(user);
            return ResponseEntity.ok(new AuthenticationResponse(jwtUtil.generateToken(user)));
        }
        return ResponseEntity.ok("user not created");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> generateToken(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println(authenticationRequest.getUsername() + authenticationRequest.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(400).body("{\"message\":\"Invalid Username or Password\"}");
        }
        final UserDetails userDetails = userService
                .getUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @DeleteMapping(path = "{studentId}")
    public void deleteUser(@PathVariable long studentId) {
        userService.deleteUser(studentId);
    }

    @PutMapping(path = "{studentId}")
    public void updateUser(@PathVariable long studentId, @RequestParam(required = false) String name, @RequestParam(required = false) String email) {
        userService.updateUser(studentId, name, email);
    }
}
