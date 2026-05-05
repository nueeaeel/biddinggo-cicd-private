package com.biddingmate.biddinggo.directinquiry.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.directinquiry.dto.DirectInquiryView;
import com.biddingmate.biddinggo.directinquiry.model.DirectInquiry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface DirectInquiryMapper extends IMybatisCRUD<DirectInquiry> {
    List<DirectInquiryView> findAllDirectInquiry(RowBounds rowBounds,
                                                 @Param("order") String sortOrder);
    List<DirectInquiryView> findDirectInquiryOfMe(RowBounds rowBounds,
                                                 @Param("memberId") long memberId,
                                                 @Param("order") String sortOrder);
    int getDirectInquiryTotal();
    int getDirectInquiryTotalOfMe(@Param("memberId") long memberId);
}
