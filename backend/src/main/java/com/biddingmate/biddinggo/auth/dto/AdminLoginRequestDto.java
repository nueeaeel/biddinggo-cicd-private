package com.biddingmate.biddinggo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
@Schema(description = "로그인 dto")
public class AdminLoginRequestDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Schema(example = "admin1")
    private final String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(example = "1234")
    private final String password;
}
