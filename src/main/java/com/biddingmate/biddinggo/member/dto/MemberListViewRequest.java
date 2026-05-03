package com.biddingmate.biddinggo.member.dto;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@SuperBuilder
@Schema(description = "관리자용 회원 목록 조회 요청 DTO")
public class MemberListViewRequest extends BasePageRequest {
    @Schema(description = "검색 키워드(닉네임/이메일)", example = "biddingmate", nullable = true)
    private String keyword;
    @Schema(description = "회원 상태", example = "ACTIVE", nullable = true)
    private MemberStatus status;
}
