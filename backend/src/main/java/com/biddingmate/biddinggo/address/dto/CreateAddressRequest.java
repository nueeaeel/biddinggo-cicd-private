package com.biddingmate.biddinggo.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "배송지 등록 요청 DTO")
public class CreateAddressRequest {
    @Schema(description = "우편번호", example = "06236")
    @NotBlank(message = "우편번호는 필수입니다.")
    @Size(max = 20, message = "우편번호는 20자 이하여야 합니다.")
    private String zipcode;

    @Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로 123")
    @NotBlank(message = "주소는 필수입니다.")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @Schema(description = "상세 주소", example = "101동 202호", nullable = true)
    @Size(max = 255, message = "상세 주소는 255자 이하여야 합니다.")
    private String detailAddress;
}
