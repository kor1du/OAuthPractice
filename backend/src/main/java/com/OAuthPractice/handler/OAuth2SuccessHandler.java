package com.OAuthPractice.handler;

import com.OAuthPractice.dto.TokenDTO;
import com.OAuthPractice.dto.UserDTO;
import com.OAuthPractice.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        log.info("토큰 발행 시작");

        String redirectUrl = makeRedirectUrl("testToken");

        log.info("redirect Url is ... " + redirectUrl);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String makeRedirectUrl(String token)
    {
        return UriComponentsBuilder
                .fromUriString("http://localhost:3000/login?token="+token)
                .build()
                .toUriString();
    }
}
