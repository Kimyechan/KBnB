package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.user.LoginRequest;
import com.buildup.kbnb.dto.user.SignUpRequest;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.TokenProvider;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserRepository userRepository;

    @Test
    @DisplayName("이메일 로그인 성공")
    public void loginEmail() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("test")
                .build();

        User user = User.builder()
                .id(1L)
                .name("test")
                .email(loginRequest.getEmail())
                .password(passwordEncoder.encode(loginRequest.getPassword()))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();

        given(userRepository.findByEmail(user.getEmail())).willReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-login-email",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("email").description("유저 이메일"),
                                fieldWithPath("password").description("유저 패스워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("JWT Access Token 값"),
                                fieldWithPath("tokenType").description("Access Token 타입"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 - 이메일 or 비밀번호 불일치")
    public void loginFail() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("test")
                .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-emailOrPasswordWrong",
                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메세지")
                        )
                ));
    }

    @Test
    @DisplayName("이메일 회원가입 성공")
    public void signupEmail() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("test")
                .birth(LocalDate.of(1999, 7, 18))
                .email("test@gmail.com")
                .password("test")
                .build();

        User user = User.builder()
                .id(1L)
                .name(signUpRequest.getName())
                .birth(signUpRequest.getBirth())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();

        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("auth-signup-email",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("name").description("유저 이름"),
                                fieldWithPath("birth").description("유저 생년월일"),
                                fieldWithPath("email").description("유저 이메일"),
                                fieldWithPath("password").description("유저 패스워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("JWT Access Token 값"),
                                fieldWithPath("tokenType").description("Access Token 타입"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패- 이메일 중복")
    public void signupFail() throws Exception{
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("test")
                .birth(LocalDate.of(1999, 7, 18))
                .email("test@gmail.com")
                .password("test")
                .build();

        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-emailDuplication",
                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메세지")
                        )
                ));
    }

}