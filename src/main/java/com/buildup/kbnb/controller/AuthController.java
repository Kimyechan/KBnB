package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.BadRequestException;
import com.buildup.kbnb.advice.exception.EmailDuplicationException;
import com.buildup.kbnb.advice.exception.EmailOrPassWrongException;
import com.buildup.kbnb.dto.AuthResponse;
import com.buildup.kbnb.dto.user.LoginRequest;
import com.buildup.kbnb.dto.user.SignUpRequest;
import com.buildup.kbnb.dto.user.SignUpResponse;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(EmailOrPassWrongException::new);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new EmailOrPassWrongException();
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
            throw new EmailDuplicationException();
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setBirth(signUpRequest.getBirth());
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
