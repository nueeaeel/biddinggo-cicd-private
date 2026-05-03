package com.biddingmate.biddinggo.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "배송지 등록 응답 DTO")
public class CreateAddressResponse {
    @Schema(description = "배송지 ID", example = "1")
    private Long id;
}
