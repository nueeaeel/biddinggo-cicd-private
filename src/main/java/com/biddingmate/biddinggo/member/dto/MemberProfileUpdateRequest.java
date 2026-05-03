package com.biddingmate.biddinggo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileUpdateRequest {
    private String nickname;
    private String imageUrl;
    private String bankCode;
    private String bankAccount;
}
