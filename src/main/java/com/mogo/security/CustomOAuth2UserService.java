package com.mogo.security;


import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;


@Log4j2
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String uri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        String tokenValue = userRequest.getAccessToken().getTokenValue();
        uri = uri + "?access_token=" + tokenValue;
        MogoOAuth2User mogoOAuth2User = null;
        try {
            mogoOAuth2User = new RestTemplate().getForObject(new URI(uri), MogoOAuth2User.class);
        } catch (URISyntaxException e) {
            log.info("Exception: {}", e);
        }
        return mogoOAuth2User;
    }
}
