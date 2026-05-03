package com.biddingmate.biddinggo.address.controller;

import com.biddingmate.biddinggo.address.dto.AddressListResponse;
import com.biddingmate.biddinggo.address.dto.CreateAddressRequest;
import com.biddingmate.biddinggo.address.dto.CreateAddressResponse;
import com.biddingmate.biddinggo.address.service.AddressService;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.member.model.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
@Tag(name = "Address", description = "배송지 관리 API")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("")
    @Operation(summary = "배송지 등록", description = "새 배송지를 등록합니다.")
    public ResponseEntity<ApiResponse<CreateAddressResponse>> createAddress(@RequestBody @Valid CreateAddressRequest request,
                                                                            @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        CreateAddressResponse result = addressService.createAddress(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "배송지 등록 성공", result);
    }

    @GetMapping("")
    @Operation(summary = "배송지 목록 조회", description = "배송지 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AddressListResponse>>> findAllAddress(@Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        List<AddressListResponse> result = addressService.findAllAddress(member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "배송지 조회 성공", result);
    }

    @PatchMapping("/{addressId}")
    @Operation(summary = "기본 배송지 변경", description = "기본 배송지로 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updateDefault(@Parameter(description = "배송지 ID", example = "1") @PathVariable Long addressId,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        addressService.updateDefaultAddress(addressId, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "기본 배송지 변경에 성공했습니다.", null);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@Parameter(description = "배송지 ID", example = "1") @PathVariable Long addressId,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        addressService.deleteAddress(addressId, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "배송지 삭제를 성공했습니다.", null);
    }
}
