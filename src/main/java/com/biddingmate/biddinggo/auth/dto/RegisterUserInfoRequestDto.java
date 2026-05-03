package com.biddingmate.biddinggo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserInfoRequestDto {

    @NotBlank(message = "이름은 필수 입니다.")
    @Schema(example ="선우팍")
    private String name;

    @NotBlank(message = "닉네임은 필수 입니다.")
    @Schema(example ="선우짱1234")
    private String nickname;

    @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
    @Schema(example ="https://pub-a42a8c25bf464f499cab51ef3c3d0136.r2.dev/temp/items/2026/04/03/51fc7aa6-0e56-46dc-873b-494571da2e3d.jpg")
    private String imageUrl;

}
