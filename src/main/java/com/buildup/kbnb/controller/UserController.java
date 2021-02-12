package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.advice.exception.ResourceNotFoundException;
import com.buildup.kbnb.dto.user.UserDto;
import com.buildup.kbnb.dto.user.UserUpdateRequest;
import com.buildup.kbnb.dto.user.UserUpdateResponse;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.mail.iap.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        UserDto userDto = UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .birth(user.getBirth())
                .emailVerified(user.getEmailVerified())
                .imageUrl(user.getImageUrl())
                .build();

        EntityModel<UserDto> model = EntityModel.of(userDto);
        model.add(linkTo(methodOn(UserController.class).getCurrentUser(userPrincipal)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-get-me").withRel("profile"));

        return ResponseEntity.ok().body(model);
    }
    @PostMapping(value = "/beforeUpdate", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> beforeUpdate(@CurrentUser UserPrincipal userPrincipal, String email, String password) {
        User user = userService.findById(userPrincipal.getId());
        Map map;
        if (email.equals(user.getEmail()) && passwordEncoder.matches(password, user.getPassword())) {
           map = new HashMap(); map.put("본인 인증 성공: ","회원정보 수정 페이지로 이동");
        }
        else throw new ReservationException("Access denied check email and password again");

        EntityModel<Map> model = EntityModel.of(map);
        model.add(linkTo(methodOn(UserController.class).beforeUpdate(userPrincipal, email, password)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-before-update").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    @PostMapping(value = "/update", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> beforeUpdate(@CurrentUser UserPrincipal userPrincipal, @RequestBody UserUpdateRequest userUpdateRequest) {
        User user = userService.findById(userPrincipal.getId());
        UserUpdateResponse userUpdateResponse = updateUserAndReturnResponseDto(user, userUpdateRequest);
        EntityModel<UserUpdateResponse> model = EntityModel.of(userUpdateResponse);
        model.add(linkTo(methodOn(UserController.class).beforeUpdate(userPrincipal, userUpdateRequest)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-update").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    public UserUpdateResponse updateUserAndReturnResponseDto(User user, UserUpdateRequest userUpdateRequest) {
        user.setEmail(userUpdateRequest.getEmail());
        user.setName(userUpdateRequest.getName());
        user.setBirth(userUpdateRequest.getBirth());
        user.setImageUrl(userUpdateRequest.getImageUrl());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        return UserUpdateResponse.builder()
                .email(user.getEmail()).id(user.getId()).birth(user.getBirth()).name(user.getName()).imageUrl(user.getImageUrl()).build();
    }
}
