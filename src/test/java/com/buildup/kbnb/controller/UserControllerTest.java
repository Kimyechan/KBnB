package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}