package com.biddingmate.biddinggo.auth.service;

import com.biddingmate.biddinggo.auth.dto.CustomOAuth2Member;
import com.biddingmate.biddinggo.auth.dto.GoogleResponse;
import com.biddingmate.biddinggo.auth.dto.KakaoResponse;
import com.biddingmate.biddinggo.auth.dto.MemberDto;
import com.biddingmate.biddinggo.auth.dto.OAuth2Response;
import com.biddingmate.biddinggo.auth.mapper.AuthSocialMapper;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;
    private final AuthSocialMapper authSocialMapper;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        } else {

            return null;
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        // JWT에서 식별자로 쓸 이름 -> 우리 db엔 social_account에 각각 들어간다.
        String username = oAuth2Response.getProvider()+oAuth2Response.getProviderId();

        Member member = authSocialMapper.findBySocialInfo(provider,providerId);

        if (member == null) {

            member = Member.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .nickname(null)
                    .role("USER")
                    .build();

            memberMapper.saveMember(member);
            authSocialMapper.saveSocialAccount(member.getId(), provider, providerId);

        } else {
            validateLoginAllowed(member);

            member.update(oAuth2Response.getName(), oAuth2Response.getEmail());
            memberMapper.updateMember(member);

        }

        MemberDto memberDto = new MemberDto();
        memberDto.setMembername(username);
        memberDto.setRole(member.getRole());
        memberDto.setName(member.getName());

        return new CustomOAuth2Member(memberDto);

    }

    private void validateLoginAllowed(Member member) {
        if (member.getStatus() == MemberStatus.DELETED || member.getStatus() == MemberStatus.INACTIVE) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("login_blocked_member"),
                    "탈퇴 또는 비활성화된 회원은 로그인할 수 없습니다."
            );
        }
    }
}
