package com.mogo.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class MogoOAuth2User implements OAuth2User, Serializable {

    private List<GrantedAuthority> authorities =
            AuthorityUtils.createAuthorityList("ROLE_USER");
    @JsonIgnore
    private Map<String, Object> attributes;

    private int depart_id;
    private String email;
    private String job_number;
    private int leader_id;
    private String mobile;
    private String nickname;
    private int oauth_id;
    private String username;

    public String getName() {
        return username;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (this.attributes == null) {
            attributes = new ObjectMapper().convertValue(this, Map.class);
        }
        return attributes;
    }
}
