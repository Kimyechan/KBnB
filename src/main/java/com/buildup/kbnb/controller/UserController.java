package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.ResourceNotFoundException;
import com.buildup.kbnb.dto.user.UserDto;
import com.buildup.kbnb.dto.user.UserImgUpdateResponse;
import com.buildup.kbnb.dto.user.UserUpdateRequest;
import com.buildup.kbnb.dto.user.UserUpdateResponse;
import com.buildup.kbnb.model.user.Role;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.swing.text.html.parser.Entity;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

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

    @PostMapping(value = "/update", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal, @RequestBody UserUpdateRequest userUpdateRequest) {
        User user = userService.findById(userPrincipal.getId());

        UserUpdateResponse userUpdateResponse = updateUserAndReturnResponseDto(user, userUpdateRequest);
        EntityModel<UserUpdateResponse> model = EntityModel.of(userUpdateResponse);
        model.add(linkTo(methodOn(UserController.class).update(userPrincipal, userUpdateRequest)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-update").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    @PostMapping(value = "/update/photo" , produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> updatePhoto(@CurrentUser UserPrincipal userPrincipal,  @RequestPart MultipartFile file) throws IOException {
        User user = userService.findById((userPrincipal.getId()));
        String newImgUrl;
        if (file == null)
            newImgUrl = "https://kbnbbucket.s3.ap-northeast-2.amazonaws.com/userImg/test";
        else {
            newImgUrl = s3Uploader.upload(file, "userImg", user.getName());
        }

        UserImgUpdateResponse userImgUpdateResponse = UserImgUpdateResponse.builder().newImgUrl(newImgUrl).build();
        EntityModel<UserImgUpdateResponse> model = EntityModel.of(userImgUpdateResponse);
        model.add(Link.of("/docs/api.html#resource-user-updatePhoto").withRel("profile"));
        return ResponseEntity.ok(model);
    }


    public UserUpdateResponse updateUserAndReturnResponseDto(User user, UserUpdateRequest userUpdateRequest) {

        user.setEmail(userUpdateRequest.getEmail());
        user.setName(userUpdateRequest.getName());
        user.setBirth(LocalDate.parse(userUpdateRequest.getBirth()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        return UserUpdateResponse.builder()
                .email(user.getEmail()).birth(user.getBirth()).name(user.getName()).build();
    }

/*    @PatchMapping(value = "/registerToHost", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public String changeToHostRole(@CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findById(userPrincipal.getId());
        user.setRole(Role.HOST.getValue());
        userService.save(user);
        return user.getRole();
    }*/
}
