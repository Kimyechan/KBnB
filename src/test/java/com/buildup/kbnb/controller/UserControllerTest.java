package com.buildup.kbnb.controller;

import com.amazonaws.util.IOUtils;
import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.user.UserUpdateRequest;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.UserService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

 /*   @Test
    @DisplayName("유저 정보 수정 전 본인 확인")
    public void beforeUpdateUserInfo() throws Exception{
        User user = createUser();
        given(userService.findById(any())).willReturn(user);
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        mockMvc.perform(post("/user/beforeUpdate")
                .param("email", "test@gmail.com")
                .param("password","test")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-before-update",
                        requestParameters(
                                parameterWithName("email").description("입력된 email"),
                                parameterWithName("password").description("입력된 password")
                        ),
                        responseFields(
                                fieldWithPath("본인 인증 성공: ").description("회원 정보 수정 페이지로 이동"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }*/
    public UserUpdateRequest userUpdateRequest(User user) {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder().password(passwordEncoder.encode("updatedPassword"))
                .name("updatedName").email("updated@google.com")
                .id(user.getId()).birth("2020-11-11").build();
        return userUpdateRequest;
    }

    @Test
    @DisplayName("유저 정보 수정")
    public void updateUserInfo() throws Exception{

        MockMultipartFile multipartFile = new MockMultipartFile("defaultImg", "testImg","image/png", "image".getBytes());
        User user = createUser();
        given(userService.findById(any())).willReturn(user);
        given(userService.save(any())).willReturn(user);
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        mockMvc.perform(fileUpload("/user/update").file(multipartFile)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(userUpdateRequest(user)))
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("id").description("유저 식별자"),
                                fieldWithPath("name").description("수정 요청할 이름"),
                                fieldWithPath("email").description("수정 요청할 이메일"),
                                fieldWithPath("birth").description("수정 요청할 생일")
                        ),
                        requestParts(
                                partWithName("defaultImg").description("테스트용 이미지")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("유저 식별자"),
                                fieldWithPath("name").description("수정 요청된 이름"),
                                fieldWithPath("email").description("수정 요청된 이메일"),
                                fieldWithPath("imageUrl").description("수정 요청된 이미지 url"),
                                fieldWithPath("birth").description("수정 요청된 생일"),

                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")

                        )
                        ));
    }
}