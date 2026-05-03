package com.biddingmate.biddinggo.auth.event;

import com.biddingmate.biddinggo.auth.jwt.JwtProvider;
import com.biddingmate.biddinggo.member.event.MemberStatusUpdateEvent;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberAuthCleanupListener {

    private final MemberMapper memberMapper;
    private final JwtProvider jwtProvider;

    @EventListener
    public void handle(MemberStatusUpdateEvent event) {
        if (event.getStatus() != MemberStatus.DELETED && event.getStatus() != MemberStatus.INACTIVE) {
            return;
        }

        String username = memberMapper.findUsernameById(event.getMemberId());
        if (username == null || username.isBlank()) {
            return;
        }

        jwtProvider.deleteRefreshTokenByUsername(username);
        log.info("[member-auth-cleanup] memberId={}, username={}, status={}",
                event.getMemberId(), username, event.getStatus());
    }
}
