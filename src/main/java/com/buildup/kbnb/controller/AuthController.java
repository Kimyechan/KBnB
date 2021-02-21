package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.EmailDuplicationException;
import com.buildup.kbnb.advice.exception.EmailOrPassWrongException;
import com.buildup.kbnb.advice.exception.UserFieldNotValidException;
import com.buildup.kbnb.dto.AuthResponse;
import com.buildup.kbnb.dto.user.LoginRequest;
import com.buildup.kbnb.dto.user.SignUpRequest;
import com.buildup.kbnb.dto.user.SignUpResponse;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
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
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult error) {
        if (error.hasErrors()) {
            throw new UserFieldNotValidException();
        }

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(EmailOrPassWrongException::new);
        userService.checkCorrectPassword(loginRequest.getPassword(), user.getPassword());

        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        EntityModel<AuthResponse> model = EntityModel.of(new AuthResponse(token));
        model.add(linkTo(methodOn(AuthController.class).authenticateUser(loginRequest, error)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-login-email").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult error) {
        if (error.hasErrors()) {
            throw new UserFieldNotValidException();
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailDuplicationException();
        }

        User user = mapDtoToUser(signUpRequest);
        User savedUser = userRepository.save(user);

        String token = tokenProvider.createToken(String.valueOf(savedUser.getId()));
        SignUpResponse response = SignUpResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .build();

        URI location = linkTo(methodOn(UserController.class).getCurrentUser(UserPrincipal.create(savedUser))).toUri();
        EntityModel<SignUpResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(AuthController.class).registerUser(signUpRequest, error)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-signup-email").withRel("profile"));

        return ResponseEntity.created(location)
                .body(model);
    }

    private User mapDtoToUser(SignUpRequest signUpRequest) {
        return User.builder()
                .name(signUpRequest.getName())
                .birth(signUpRequest.getBirth())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .imageUrl("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-16T12%3A11%3A25.400507.png")
                .build();
    }
}
