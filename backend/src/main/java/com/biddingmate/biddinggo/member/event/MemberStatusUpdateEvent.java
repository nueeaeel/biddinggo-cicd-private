package com.biddingmate.biddinggo.member.event;

import com.biddingmate.biddinggo.member.model.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberStatusUpdateEvent {
    private Long memberId;
    private MemberStatus status;
}
