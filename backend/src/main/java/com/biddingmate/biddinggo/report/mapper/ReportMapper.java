package com.biddingmate.biddinggo.report.mapper;

import com.biddingmate.biddinggo.report.model.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportMapper {
    int insertReport(Report report);
    int countByTargetMemberId(@Param("targetMemberId") Long targetMemberId);
    boolean existsByReporterIdAndTargetId(
            @Param("reporterId") Long reporterId,
            @Param("targetId") Long targetId,
            @Param("targetType") String targetType
    );
}