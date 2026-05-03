package com.biddingmate.biddinggo.member.dto;

import com.biddingmate.biddinggo.member.model.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자용 회원 상태 변경 요청 DTO")
public class UpdateMemberStatusRequest {
    @Schema(description = "변경할 회원 상태", example = "INACTIVE")
    private MemberStatus status;
}
