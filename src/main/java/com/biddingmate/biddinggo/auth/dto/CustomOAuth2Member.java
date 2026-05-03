package com.biddingmate.biddinggo.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2Member implements OAuth2User {

    private final MemberDto memberDto;

    public CustomOAuth2Member(MemberDto memberDto) {

        this.memberDto = memberDto;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {

                return memberDto.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return memberDto.getName();
    }

    public String getMembername() {

        return memberDto.getMembername();
    }
}
