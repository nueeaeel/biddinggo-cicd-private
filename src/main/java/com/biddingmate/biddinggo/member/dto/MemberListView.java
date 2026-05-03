package com.biddingmate.biddinggo.member.dto;

import com.biddingmate.biddinggo.member.model.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "관리자용 회원 목록 조회 응답 DTO")
public class MemberListView {
    @Schema(description = "회원 ID", example = "1")
    private Long id;
    @Schema(description = "닉네임", example = "biddingmate")
    private String nickname;
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    @Schema(description = "회원 상태", example = "ACTIVE")
    private MemberStatus status;
    @Schema(description = "가입 일시", example = "2026-04-05T12:30:00")
    private LocalDateTime createdAt;

    // 거래 건수 필드는 추후에 추가 예정
    // private int dealTotalCount;
}
