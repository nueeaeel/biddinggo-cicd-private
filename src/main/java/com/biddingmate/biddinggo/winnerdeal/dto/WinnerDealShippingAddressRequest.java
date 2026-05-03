package com.biddingmate.biddinggo.winnerdeal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "낙찰 거래 배송지 등록 요청 DTO")
public class WinnerDealShippingAddressRequest {
    @Schema(description = "우편번호", example = "06109")
    @NotBlank(message = "우편번호는 필수입니다.")
    @Size(max = 20, message = "우편번호는 20자 이하여야 합니다.")
    private String zipcode;

    @Schema(description = "기본 주소", example = "서울 강남구 테헤란로 152")
    @NotBlank(message = "주소는 필수입니다.")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @Schema(description = "상세 주소", example = "강남파이낸스센터 12층")
    @Size(max = 255, message = "상세 주소는 255자 이하여야 합니다.")
    private String detailAddress;

    @Schema(description = "수령인", example = "홍길동")
    @NotBlank(message = "수령인은 필수입니다.")
    @Size(max = 20, message = "수령인은 20자 이하여야 합니다.")
    private String recipient;

    @Schema(description = "연락처", example = "010-1234-5678")
    @NotBlank(message = "연락처는 필수입니다.")
    @Size(max = 20, message = "연락처는 20자 이하여야 합니다.")
    private String tel;
}
