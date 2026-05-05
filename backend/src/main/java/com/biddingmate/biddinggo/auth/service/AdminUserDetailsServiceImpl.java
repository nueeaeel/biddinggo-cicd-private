package com.biddingmate.biddinggo.auth.service;

import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberMapper.selectMemberByUsername(username);

        if (member == null) {

            throw new UsernameNotFoundException(String.format("%s not found",username));
        }

        if (member.getStatus() == MemberStatus.DELETED || member.getStatus() == MemberStatus.INACTIVE) {
            throw new DisabledException(String.format("%s is blocked", username));
        }

        return member;
    }
}
