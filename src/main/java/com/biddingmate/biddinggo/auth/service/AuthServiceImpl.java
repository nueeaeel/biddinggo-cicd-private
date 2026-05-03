package com.biddingmate.biddinggo.auth.service;

import com.biddingmate.biddinggo.auth.dto.AdminSignupRequestDto;
import com.biddingmate.biddinggo.auth.dto.LoginResponse;
import com.biddingmate.biddinggo.auth.dto.SocialInfoUpdateDto;
import com.biddingmate.biddinggo.auth.jwt.JwtProvider;
import com.biddingmate.biddinggo.auth.jwt.JwtUtil;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProvider jwtProvider;

    @Override
    public LoginResponse login(String username, String password) {
        // 사용자의 아이디와 비밀번호로 인증 처리를 진행한다.
        // 1. username으로 사용자를 조회
        Member member = memberMapper.selectMemberByUsername(username);

        // 2. PasswordEncoder를 사용해 데이터베이스에 저장된 비밀번호와 입력받은 비밀번호가 일치하는지 확인
        if (member == null || !passwordEncoder.matches(password, member.getPassword())) {
            log.warn("[login-failed] username : {}", username);
            throw new CustomException(ErrorType.INVALID_CREDENTIALS);
        }

        validateLoginAllowed(member);

        log.info("[Adminlogin] username : {}", username);

        // 3. LoginResponse 객체를 생성해서 반환
        return createLoginResponse(member);
    }

    @Override
    public void signup(AdminSignupRequestDto signupRequestDto) {

        // 아이디 중복 체크
        if (memberMapper.selectMemberByUsername(signupRequestDto.getUsername()) != null) {
            throw new CustomException(ErrorType.DUPLICATE_USERNAME);
        }

        // 이메일 중복 체크
        if (memberMapper.selectMemberByEmail(signupRequestDto.getEmail()) != null) {
            throw new CustomException(ErrorType.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 체크
        if (memberMapper.selectMemberByNickname(signupRequestDto.getNickname()) != null) {
            throw new CustomException(ErrorType.DUPLICATE_NICKNAME);
        }

        Member member = Member.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())
                .nickname(signupRequestDto.getNickname())
                .role("ADMIN")
                .status(MemberStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("[signup] username : {}", signupRequestDto.getUsername());

        memberMapper.insert(member);

    }

    @Override
    public void logout(String bearerToken) {
        String accessToken = jwtProvider.resolveToken(bearerToken);

        if (accessToken == null || accessToken.isBlank()) {
            log.info("[logout] access token not provided");
            return;
        }

        try {
            jwtProvider.addBlacklist(accessToken);
            jwtProvider.deleteRefreshToken(accessToken);

            String username = jwtUtil.getUsername(accessToken);
            log.info("[logout] username : {}", username);
        } catch (RuntimeException e) {
            log.warn("[logout] access token cleanup skipped: {}", e.getMessage());
        }

    }

    @Override
    public String createRefreshToken(String username) {
        return jwtProvider.createRefreshToken(username);
    }

    @Override
    public LoginResponse refreshAccessToken(String refreshToken) {

        if (refreshToken.isBlank() || !jwtUtil.validateToken(refreshToken)) {

            throw new CustomException(ErrorType.REFRESH_TOKEN_INVALID);
        }

        if (!jwtProvider.isValidRefresh(refreshToken)) {

            throw new CustomException(ErrorType.REFRESH_TOKEN_INVALID);

        }

        Member member = memberMapper.selectMemberByUsername(jwtUtil.getUsername(refreshToken));

        validateLoginAllowed(member);

        return createLoginResponse(member);

    }

    @Override
    public void updateInfo(String username, String name, String nickname, String imageUrl) {

        Member member = memberMapper.selectMemberByUsername(username);
        // 맴버 중복 체크
        if (member == null) {

            throw new CustomException(ErrorType.USER_NOT_FOUND);
        }

        // 닉네임 중복 체크
        if (memberMapper.selectMemberByNickname(nickname) != null) {

            throw new CustomException(ErrorType.DUPLICATE_NICKNAME);

        }

        // 유저가 PENDING인지 체크
        if (member.getStatus() != MemberStatus.PENDING) {

            throw new CustomException(ErrorType.ALREADY_REGISTERED_USER);
        }

        SocialInfoUpdateDto updateDto = SocialInfoUpdateDto.builder()
                .username(username)
                .name(name)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .status(MemberStatus.ACTIVE)
                .build();

        memberMapper.updateMemberInfo(updateDto);

    }

    private LoginResponse createLoginResponse(Member member) {

        // 사용자 권한 추출
        List<String> authorities =
                member.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        // 엑세스토큰 발급
        String accessToken =
                jwtProvider.createAccessToken(member.getUsername(), authorities, member.getStatus().name());


        return LoginResponse.builder()
                .accessToken(accessToken)
                .type("Bearer")
                .memberId(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .status(member.getStatus().name())
                .authorities(authorities)
                .issuedAt(jwtUtil.getIssuedAt(accessToken))
                .expiredAt(jwtUtil.getExpiredAt(accessToken))
                .build();
    }

    private void validateLoginAllowed(Member member) {
        if (member == null) {
            throw new CustomException(ErrorType.USER_NOT_FOUND);
        }

        if (member.getStatus() == MemberStatus.DELETED || member.getStatus() == MemberStatus.INACTIVE) {
            jwtProvider.deleteRefreshTokenByUsername(member.getUsername());
            throw new CustomException(ErrorType.FORBIDDEN);
        }
    }
}
