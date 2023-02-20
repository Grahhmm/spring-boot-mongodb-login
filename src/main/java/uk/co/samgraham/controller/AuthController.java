package uk.co.samgraham.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.samgraham.model.ERole;
import uk.co.samgraham.model.Role;
import uk.co.samgraham.model.User;
import uk.co.samgraham.payload.request.LoginRequest;
import uk.co.samgraham.payload.request.SignupRequest;
import uk.co.samgraham.payload.response.MessageResponse;
import uk.co.samgraham.payload.response.UserInfoResponse;
import uk.co.samgraham.repository.RoleRepository;
import uk.co.samgraham.repository.UserRepository;
import uk.co.samgraham.security.jwt.JwtService;
import uk.co.samgraham.security.userdetails.UserDetailsImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

  AuthenticationManager authenticationManager;
  UserRepository userRepository;
  RoleRepository roleRepository;
  PasswordEncoder encoder;
  JwtService jwtService;

  public AuthController(
          AuthenticationManager authenticationManager,
          UserRepository userRepository,
          RoleRepository roleRepository,
          PasswordEncoder encoder,
          JwtService jwtService
  ) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.encoder = encoder;
    this.jwtService = jwtService;
  }

  @PostMapping("/signin")
  public ResponseEntity<UserInfoResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtService.generateJwtCookie(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(UserInfoResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles).build());
  }

  @PostMapping("/signup")
  public ResponseEntity<MessageResponse> registerUser(@RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(MessageResponse.builder().message("Error: Username is already taken!").build());
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
              .body(MessageResponse.builder().message("Error: Email is already in use!").build());
    }

    // Create new user's account
    User user = User.builder()
            .username(signUpRequest.getUsername())
            .email(signUpRequest.getEmail())
            .password(encoder.encode(signUpRequest.getPassword())).build();

    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin" -> {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
          }
          case "mod" -> {
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);
          }
          default -> {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
          }
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(MessageResponse.builder().message("User registered successfully!").build());
  }
}
