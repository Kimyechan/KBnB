package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.user.BirthDto;
import com.buildup.kbnb.dto.user.EmailDto;
import com.buildup.kbnb.dto.user.NameDto;
import com.buildup.kbnb.dto.user.UserUpdateRequest;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.util.S3Uploader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    TokenProvider tokenProvider;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService userService;

    @MockBean
    S3Uploader s3Uploader;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public User createUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .birth(LocalDate.of(1999, 7, 18))
                .email("test@gmail.com")
                .password(passwordEncoder.encode("test"))
                .imageUrl("Image URL")
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();

        given(customUserDetailsService.loadUserById(user.getId()))
                .willReturn(UserPrincipal.create(user));

        return user;
    }

    public User createUserFail() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .birth(LocalDate.of(1999, 7, 18))
                .email("test@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();

        given(customUserDetailsService.loadUserById(user.getId()))
                .willReturn(UserPrincipal.create(user));

        return user;
    }

    @Test
    @DisplayName("유저 개인정보 확인")
    public void getUserInfo() throws Exception {

        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        given(userRepository.findById(user.getId())).willReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/user/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-get-me",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 인증 토큰"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("name").description("유저 이름"),
                                fieldWithPath("email").description("유저 이메일"),
                                fieldWithPath("birth").description("유저 생년월일"),
                                fieldWithPath("imageUrl").description("유저 이미지 URL"),
                                fieldWithPath("emailVerified").description("이메일 인증 여부"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }


    public UserUpdateRequest userUpdateRequest() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .name("updatedName")
                .email("updated@google.com")
                .birth("2020-11-11")
                .build();
        return userUpdateRequest;
    }

    public UserUpdateRequest userUpdateRequestFail() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .name("test")
                .email("test@gmail.com")
                .birth("2020-11-11")
                .build();
        return userUpdateRequest;
    }

    @Test
    @DisplayName("유저 정보 수정")
    public void updateUserInfo() throws Exception {
        User user = createUser();
        given(userService.findById(any())).willReturn(user);
        given(userService.save(any())).willReturn(user);
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        mockMvc.perform(post("/user/update")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(userUpdateRequest()))
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정 요청할 이름"),
                                fieldWithPath("email").description("수정 요청할 이메일"),
                                fieldWithPath("birth").description("수정 요청할 생일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("name").description("수정 요청된 이름"),
                                fieldWithPath("email").description("수정 요청된 이메일"),
                                fieldWithPath("birth").description("수정 요청된 생일"),

                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")

                        )
                ));
    }

    @Test
    @DisplayName("유저 정보 수정 실패-이메일 중복")
    public void updateUserInfoFail() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        given(userService.findById(any())).willReturn(user);
        given(userRepository.existsByEmail(any())).willReturn(true);

        mockMvc.perform(post("/user/update")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(userUpdateRequestFail()))
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-userUpdateEmailDuplication",

                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메세지")
                        )
                ));
    }

    @Test
    @DisplayName("유저 이미지 변경 테스트")
    public void updatePhoto() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(s3Uploader.upload(any(), any(), any())).willReturn("test url");

        mockMvc.perform(fileUpload("/user/update/photo").file("file.jpg", "example".getBytes())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)

        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-updatePhoto",
                        requestParts(
                                partWithName("file").description("변경될 이미지")
                        ),
                        responseFields(
                                fieldWithPath("newImgUrl").description("새로운 이미지URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }

   /* @Test
    @DisplayName("유저 이미지 변경 실패 테스트")
    public void updatePhotoFail() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(s3Uploader.upload(any(), any(), any())).willReturn("test url");
        MockMultipartFile mockFile = new MockMultipartFile()
        mockMvc.perform(fileUpload("/user/update/photo").file("file", "example".getBytes())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)

        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-updatePhoto",
                        requestParts(
                                partWithName("file").description("변경될 이미지")
                        ),
                        responseFields(
                                fieldWithPath("newImgUrl").description("새로운 이미지URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }*/

    @Test
    @DisplayName("유저 사진 가져오기 테스트")
    public void getPhoto() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);

        mockMvc.perform(get("/user/photo")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-getPhoto",
                        responseFields(
                                fieldWithPath("url").description("사용자 사진 url"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }

    @Test
    @DisplayName("유저 사진 가져오기 실패 테스트")
    public void getPhotoFail() throws Exception {
        User user = createUserFail();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);

        mockMvc.perform(get("/user/photo")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-urlNotExist",
                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저 이메일 변경 테스트")
    public void updateEmail() throws Exception {
        User user = createUser();
        user.setEmail("newEmail@test.com");
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userService.save(any())).willReturn(user);
        EmailDto emailDto = EmailDto.builder()
                .email("newEmail@test.com")
                .build();
        mockMvc.perform(post("/user/update/email")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update-email",
                        requestFields(
                                fieldWithPath("email").description("변경 요청 이메일")
                        ),
                        responseFields(
                                fieldWithPath("email").description("변경 완료 이메일"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }

    @Test
    @DisplayName("이메일 변경 실패 테스트")
    public void updateEmailFail() throws Exception {
        User user = createUser();
        user.setEmail("newEmail@test.com");
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(userRepository.existsByEmail(anyString())).willReturn(true);
        given(userService.save(any())).willReturn(user);

        EmailDto emailDto = EmailDto.builder()
                .email("newEmail@test.com")
                .build();

        mockMvc.perform(post("/user/update/email")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-userUpdate-emailDuplicate",
                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저 이름 변경 테스트")
    public void updateName() throws Exception {
        User user = createUser();
        given(userService.findById(any())).willReturn(user);
        given(userService.save(any())).willReturn(user);
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        NameDto emailDto = NameDto.builder()
                .name("newName")
                .build();

        mockMvc.perform(post("/user/update/name")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update-name",
                        requestFields(
                                fieldWithPath("name").description("변경 요청 이름")
                        ),
                        responseFields(
                                fieldWithPath("name").description("변경된 이름"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )));
    }

    @Test
    @DisplayName("유저 이름 변경 실패 테스트")
    public void updateNameFail() throws Exception {
        User user = createUser();
        given(userService.findById(any())).willReturn(user);
        given(userService.save(any())).willReturn(user);
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        NameDto emailDto = NameDto.builder()
                .name("")
                .build();

        mockMvc.perform(post("/user/update/name")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-userUpdate-blankName",
                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메시지")
                        )));
    }

    @Test
    @DisplayName("유저 생일 변경 테스트")
    public void updateBirth() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(userService.save(any())).willReturn(user);

        BirthDto birthDto = BirthDto.builder()
                .birth("2020-02-02")
                .build();

        mockMvc.perform(post("/user/update/birth")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(birthDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update-birth",
                        requestFields(
                                fieldWithPath("birth").description("변경 요청 생일")
                        ),
                        responseFields(
                                fieldWithPath("birth").description("변경 완료된 생일"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )));
    }

    @Test
    @DisplayName("유저 생일 변경 실패")
    public void updateBirthFail() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(userService.save(any())).willReturn(user);

        BirthDto birthDto = BirthDto.builder()
                .birth("2020-02-020")
                .build();

        mockMvc.perform(post("/user/update/birth")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(birthDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-userUpdate-WrongDateFormat",
                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메시지")
                        )));
    }
}