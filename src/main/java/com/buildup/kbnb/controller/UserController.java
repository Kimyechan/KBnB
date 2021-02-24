package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.*;
import com.buildup.kbnb.dto.user.BirthDto;
import com.buildup.kbnb.dto.user.*;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
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
        if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
            throw new EmailDuplicationException();
        }
        UserUpdateResponse userUpdateResponse = updateUserAndReturnResponseDto(user, userUpdateRequest);
        EntityModel<UserUpdateResponse> model = EntityModel.of(userUpdateResponse);
        model.add(linkTo(methodOn(UserController.class).update(userPrincipal, userUpdateRequest)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-user-update").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    @PostMapping(value = "/update/email", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> updateEmail(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody EmailDto emailDto, BindingResult error) {
        User user = userService.findById(userPrincipal.getId());
        if (error.hasErrors())
            throw new EmailOrPassWrongException("email양식에 맞지 않습니다.");
        if (userRepository.existsByEmail(emailDto.getEmail()))
            throw new EmailDuplicationException();

        user.setEmail(emailDto.getEmail());
        userService.save(user);

        EntityModel<EmailDto> model = EntityModel.of(emailDto);
        model.add(Link.of("/docs/api.html#resource-user-update-email").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    @PostMapping(value = "/update/name", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> updateName(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody NameDto nameDto, BindingResult error) {
        if (error.hasFieldErrors("name"))
            throw new UserFieldNotValidException("변경될 이름을 기입해주세요");
        User user = userService.findById(userPrincipal.getId());

        user.setName(nameDto.getName());
        userService.save(user);

        EntityModel<NameDto> model = EntityModel.of(nameDto);
        model.add(Link.of("/docs/api.html#resource-user-update-email").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    @PostMapping(value = "/update/birth", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> updateBirth(@CurrentUser UserPrincipal userPrincipal, @RequestBody BirthDto birthDto) {
        LocalDate newBirth;
        newBirth = LocalDate.parse(birthDto.getBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        User user = userService.findById(userPrincipal.getId());

        user.setBirth(newBirth);
        userService.save(user);

        EntityModel<BirthDto> model = EntityModel.of(birthDto);
        model.add(Link.of("/docs/api.html#resource-user-update-email").withRel("profile"));
        return ResponseEntity.ok(model);

    }

    @PostMapping(value = "/update/photo", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> updatePhoto(@CurrentUser UserPrincipal userPrincipal, @Nullable @RequestPart MultipartFile file) throws IOException {
        if(!(file==null||file.getContentType().contains("image")))
            throw new TypeMissMatchException("이미지 파일이 아닙니다.");
        User user = userService.findById((userPrincipal.getId()));
        String newImgUrl;
        if (file == null)
            newImgUrl = "https://kbnbbucket.s3.ap-northeast-2.amazonaws.com/userImg/test";
        else {
            newImgUrl = s3Uploader.upload(file, "userImg", user.getId() + "-" + user.getName());
        }
        
        user.setImageUrl(newImgUrl);
        userService.save(user);
        UserImgUpdateResponse userImgUpdateResponse = UserImgUpdateResponse.builder().newImgUrl(newImgUrl).build();
        EntityModel<UserImgUpdateResponse> model = EntityModel.of(userImgUpdateResponse);
        model.add(Link.of("/docs/api.html#resource-user-updatePhoto").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    public UserUpdateResponse updateUserAndReturnResponseDto(User user, UserUpdateRequest userUpdateRequest) {
        user.setEmail(userUpdateRequest.getEmail());
        user.setName(userUpdateRequest.getName());
        user.setBirth(LocalDate.parse(userUpdateRequest.getBirth()));

        userService.save(user);

        return UserUpdateResponse.builder()
                .email(user.getEmail()).birth(user.getBirth()).name(user.getName()).build();
    }

    @GetMapping(value = "/photo", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getPhoto(@CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findById(userPrincipal.getId());

        if (user.getImageUrl() == null)
            throw new UserFieldNotValidException("url not exist");
        GetPhotoResponse getPhotoResponse = GetPhotoResponse.builder()
                .url(user.getImageUrl())
                .build();

        EntityModel<GetPhotoResponse> model = EntityModel.of(getPhotoResponse);
        model.add(Link.of("/docs/api.html#resource-user-getPhoto").withRel("profile"));
        return ResponseEntity.ok(model);
    }
}
