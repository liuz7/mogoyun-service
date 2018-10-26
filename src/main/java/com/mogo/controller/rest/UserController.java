package com.mogo.controller.rest;


import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Log4j2
@Api(description = "Set of endpoints for users.")
public class UserController {

    @GetMapping("/current")
    public Object getUser() {
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        OAuth2User principal = authentication.getPrincipal();
        return principal.getAttributes();
    }
}
