package com.OAuthPractice.service;

import com.OAuthPractice.domain.OAuth2CustomUser;
import com.OAuthPractice.domain.OAuthAttributes;
import com.OAuthPractice.entity.Member;
import com.OAuthPractice.enums.Role;
import com.OAuthPractice.oAuth2.OAuth2Attribute;
import com.OAuthPractice.repository.MemberRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>
{
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest); //OAuth2의 유저 정보를 가져온다

        Map<String, Object> originAttributes = oAuth2User.getAttributes();  //OAuth2User의 attribute들

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //OAuth2 서비스 제공업체 (google, kakao, naver)

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, originAttributes);
        saveMember(attributes);
        String email = attributes.getEmail();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new OAuth2CustomUser(registrationId, originAttributes, authorities, email);
    }

    @SneakyThrows
    private void saveMember(OAuthAttributes authAttributes)
    {
        try {
            Optional<Member> member = memberRepository
                            .findByEmail(authAttributes.getEmail());

            if(member.isEmpty())
            {
                log.info("회원가입을 진행합니다!");
                memberRepository.save(Member
                                        .builder()
                                        .name(authAttributes.getName())
                                        .email(authAttributes.getEmail())
                                        .gender(authAttributes.getGender())
                                        .age(authAttributes.getAge())
                                        .profileImageUrl(authAttributes.getProfileImageUrl())
                                        .role(Role.USER)
                                        .build());
                log.info("회원가입이 완료되었습니다!");
            }
        }catch(Exception e)
        {
            throw e;
        }
    }
}
