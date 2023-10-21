package com.OAuthPractice.configuration;

import com.OAuthPractice.handler.OAuth2SuccessHandler;
import com.OAuthPractice.service.CustomOAuth2UserService;
import com.OAuthPractice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig
{
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().
                requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception
    {
        http.
            csrf(AbstractHttpConfigurer::disable)
            .sessionManagement((sessionManagement)->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new MvcRequestMatcher(introspector,"/")).permitAll()
                .anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2
                                    .defaultSuccessUrl("/login-success")
                                    .successHandler(oAuth2SuccessHandler)
                                    .userInfoEndpoint(userInfo -> userInfo
                                    .userService(oAuth2UserService)))
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
