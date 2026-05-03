package com.biddingmate.biddinggo.inspection.dto;

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
@Schema(description = "검수 배송 정보 등록 요청 DTO")
public class UpdateInspectionShippingRequest {
    @Schema(description = "택배사", example = "CJ대한통운")
    @NotBlank(message = "택배사는 필수입니다.")
    @Size(max = 20, message = "택배사는 20자 이하여야 합니다.")
    private String carrier;

    @Schema(description = "송장 번호", example = "1234567890")
    @NotBlank(message = "송장 번호는 필수입니다.")
    @Size(max = 255, message = "송장 번호는 255자 이하여야 합니다.")
    private String trackingNumber;
}
