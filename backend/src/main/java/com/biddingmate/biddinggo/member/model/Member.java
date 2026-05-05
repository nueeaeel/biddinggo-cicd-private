package com.biddingmate.biddinggo.member.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String imageUrl;
    private Long point;
    private String bankCode;
    private String bankAccount;
    private String grade;
    private String role;
    private MemberStatus status;
    private LocalDateTime lastChangeNick;
    private LocalDateTime createdAt;

    public void update(String name, String email) {

        this.name = name;
        this.email = email;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // role이 null일 경우를 대비해 예외 처리
        if (this.role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // UserDetails.super 삭제
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // UserDetails.super 삭제
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // UserDetails.super 삭제
    }

    @Override
    public boolean isEnabled() {
        return true; // UserDetails.super 삭제
    }
}
