package com.sahilasopa.authentication.user;

import com.sahilasopa.authentication.auth.JwtUtil;
import com.sahilasopa.authentication.response.AuthenticationRequest;
import com.sahilasopa.authentication.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
                return ResponseEntity.status(400).body(new Response(e.getMessage()).getResponse());
            }
            System.out.println(ResponseEntity.ok(new Response(jwtUtil.generateToken(user))));
            return ResponseEntity.ok(new Response(jwtUtil.generateToken(user)).getResponse());
        }
        return ResponseEntity.status(400).body(new Response("Password is required").getResponse());
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> generateToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(400).body(new Response("Invalid Username or Password").getResponse());
        }
        final UserDetails userDetails = userService
                .getUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new Response(jwt).getResponse());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader String authorization) {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            User user = userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7)));
            userService.deleteUser(user.getId());
            return ResponseEntity.ok(new Response("User deleted successfully"));
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(String.valueOf(new Response("The authorization token is invalid").getResponse()));
    }

    @PostMapping(path = "/update")
    public ResponseEntity<?> updateUser(@RequestParam(required = false) String name, @RequestParam(required = false) String email, @RequestHeader String authorization) {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            try {
                userService.updateUser(userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7))).getId(), name, email);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body(new Response(e.getMessage()).getResponse());
            }
            return ResponseEntity.ok(new Response("User updated successfully").getResponse());
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("The authorization token is invalid").getResponse());
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<?> getProfile(@RequestHeader String authorization) {
        if (jwtUtil.validateToken(authorization.substring(7))) {
            User user = userService.getUserByUsername(jwtUtil.extractUsername(authorization.substring(7)));
            return ResponseEntity.ok(new Response(user.toString()));
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(String.valueOf(new Response("The authorization token is invalid").getResponse()));
    }
}
