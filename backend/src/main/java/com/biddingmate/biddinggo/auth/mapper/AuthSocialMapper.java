package com.biddingmate.biddinggo.auth.mapper;

import com.biddingmate.biddinggo.member.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthSocialMapper {

    Member findBySocialInfo(@Param("provider") String provider,
                            @Param("providerId") String providerId);

    int saveSocialAccount(@Param("memberId") Long memberId,
                           @Param("provider") String provider,
                           @Param("providerId") String providerId);

}
