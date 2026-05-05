package com.biddingmate.biddinggo.point.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.point.dto.PointHistoryDto;
import com.biddingmate.biddinggo.point.model.PointHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface PointHistoryMapper extends IMybatisCRUD<PointHistory> {
    List<PointHistoryDto> findByMemberId(RowBounds rowBounds,
                                         @Param("order") String sortOrder,
                                         @Param("memberId") Long memberId);
    int countByMemberId(@Param("memberId") Long memberId);
}
