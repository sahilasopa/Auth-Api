package com.sahilasopa.chat.user;

import com.sahilasopa.chat.auth.AuthenticationRequest;
import com.sahilasopa.chat.auth.AuthenticationResponse;
import com.sahilasopa.chat.auth.JwtUtil;
import com.sahilasopa.chat.exceptions.InvalidJwtTokenException;
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
    public List<User> getAllUsers() {
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

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader String authorization) throws InvalidJwtTokenException {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            User user = userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7)));
            userService.deleteUser(user.getId());
            return ResponseEntity.ok("User deleted successfully");
        } else
            throw new InvalidJwtTokenException("The authorization token is invalid");
    }

    @PutMapping(path = "{studentId}")
    public ResponseEntity<String> updateUser(@PathVariable long studentId, @RequestParam(required = false) String name, @RequestParam(required = false) String email, @RequestHeader String authorization) throws InvalidJwtTokenException {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            userService.updateUser(studentId, name, email);
            return ResponseEntity.ok("User updated successfully");
        } else
            throw new InvalidJwtTokenException("The authorization token is invalid");
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<User> getProfile(@RequestHeader String authorization) throws InvalidJwtTokenException {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            return ResponseEntity.ok(userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7))));
        } else
            throw new InvalidJwtTokenException("The authorization token is invalid");
    }
}
