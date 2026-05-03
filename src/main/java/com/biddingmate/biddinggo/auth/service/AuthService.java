package com.biddingmate.biddinggo.auth.service;

import com.biddingmate.biddinggo.auth.dto.AdminSignupRequestDto;
import com.biddingmate.biddinggo.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(String username, String password);

    void signup(AdminSignupRequestDto signupRequestDto);

    void logout(String bearerToken);

    String createRefreshToken(String username);

    LoginResponse refreshAccessToken(String refreshToken);

    void updateInfo(String username, String name, String nickname, String imageUrl);
}
