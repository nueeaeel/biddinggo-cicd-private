package com.biddingmate.biddinggo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Schema(description = "회원가입 dto")
public class AdminSignupRequestDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Schema(type = "아이디", example = "admin1")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(type = "비밀번호", example = "1234")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Schema(type = "이름", example = "박선우")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Schema(type = "이메일", example = "tjsdnWKd123@gmail.com")
    private String email;

    @NotBlank(message = "닉네일은 필수 입력 값입니다.")
    @Schema(type = "닉네일", example = "선우짱123")
    private String nickname;

}
