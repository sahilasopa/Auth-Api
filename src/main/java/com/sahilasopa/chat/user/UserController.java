package com.sahilasopa.chat.user;

import com.sahilasopa.chat.auth.JwtUtil;
import com.sahilasopa.chat.response.AuthenticationRequest;
import com.sahilasopa.chat.response.AuthenticationResponse;
import com.sahilasopa.chat.response.ErrorResponse;
import com.sahilasopa.chat.response.SuccessResponse;
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
            try {
                userService.addNewUser(user);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage(), 400).getResponse());
            }
            jwtUtil.generateToken(user);
            return ResponseEntity.ok(new AuthenticationResponse(jwtUtil.generateToken(user)));
        }
        return ResponseEntity.status(400).body(new ErrorResponse("Password is required", 400).getResponse());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> generateToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("Invalid Username or Password", 400).getResponse());
        }
        final UserDetails userDetails = userService
                .getUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader String authorization) {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            User user = userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7)));
            userService.deleteUser(user.getId());
            return ResponseEntity.ok(new SuccessResponse("User deleted successfully", 200));
        } else
            return ResponseEntity.status(401).body(String.valueOf(new ErrorResponse("The authorization token is invalid", 401).getResponse()));
    }

    @PutMapping(path = "update")
    public ResponseEntity<?> updateUser(@RequestParam(required = false) String name, @RequestParam(required = false) String email, @RequestHeader String authorization) {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            userService.updateUser(userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7))).getId(), name, email);
            return ResponseEntity.ok(new SuccessResponse("User updated successfully", 200).getResponse());
        } else
            return ResponseEntity.status(401).body(new ErrorResponse("The authorization token is invalid", 401).getResponse());
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<?> getProfile(@RequestHeader String authorization) {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            User user = userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7)));
            return ResponseEntity.ok(new SuccessResponse(user.toString(), 200));
        } else
            return ResponseEntity.status(401).body(String.valueOf(new ErrorResponse("The authorization token is invalid", 401).getResponse()));
    }
}
