package com.OAuthPractice.domain;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthAttributes
{
    private Map<String, Object> attributes; //OAuth2 유저 정보
    private String nameAttributesKey;
    private String name; //이름
    private String email; //이메일
    private String gender; //성별
    private String age; //나이
    private String profileImageUrl; //프로필 이미지 경로

    public static OAuthAttributes of(String socialName, Map<String, Object> attributes)
    {
        if(socialName.equals("kakao")) //카카오 로그인
        {
            return ofKakao("id", attributes);
        } else if(socialName.equals("google")) //구글 로그인
        {
            return ofGoogle("sub", attributes);
        } else if(socialName.equals("naver")) //네이버 로그인
        {
            return ofNaver("id", attributes);
        }

        return null;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes)
    {
        return OAuthAttributes
                .builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .profileImageUrl(String.valueOf(attributes.get("picture")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes)
    {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) attributes.get("properties");

        return OAuthAttributes
                .builder()
                .name(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .gender(String.valueOf(kakaoAccount.get("gender")))
                .age(String.valueOf(kakaoAccount.get("age_range")))
                .profileImageUrl(String.valueOf(kakaoProfile.get("profile_image")))
                .nameAttributesKey(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
            .name(String.valueOf(response.get("nickname")))
            .email(String.valueOf(response.get("email")))
            .profileImageUrl(String.valueOf(response.get("profile_image")))
            .age((String) response.get("age"))
            .gender((String) response.get("gender"))
            .attributes(response)
            .nameAttributesKey(userNameAttributeName)
            .build();
    }
}
