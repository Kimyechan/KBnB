package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.*;
import com.buildup.kbnb.exception.BadRequestException;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new BadRequestException("email or password wrong"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("email or password wrong");
        }

        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        EntityModel<AuthResponse> model = EntityModel.of(new AuthResponse(token));
        model.add(linkTo(methodOn(AuthController.class).authenticateUser(loginRequest)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-login-email").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        SignUpResponse response = SignUpResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .build();

        EntityModel<SignUpResponse> model = EntityModel.of(response);
        URI location = linkTo(methodOn(UserController.class).getCurrentUser(UserPrincipal.create(savedUser))).toUri();
        model.add(linkTo(methodOn(AuthController.class).registerUser(signUpRequest)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-signup-email").withRel("profile"));

        return ResponseEntity.created(location)
                .body(model);
    }

}
