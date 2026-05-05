package com.biddingmate.biddinggo.inspection.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.inspection.dto.AdminInspectionListRequest;
import com.biddingmate.biddinggo.inspection.dto.AdminInspectionListResponse;
import com.biddingmate.biddinggo.inspection.dto.InspectionDetailResponse;
import com.biddingmate.biddinggo.inspection.dto.InspectionListResponse;
import com.biddingmate.biddinggo.inspection.model.Inspection;
import com.biddingmate.biddinggo.inspection.model.InspectionStatus;
import com.biddingmate.biddinggo.item.model.ItemInspectionStatus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface InspectionMapper extends IMybatisCRUD<Inspection> {
    int updateShippingInfo(
            @Param("inspectionId") Long inspectionId,
            @Param("carrier") String carrier,
            @Param("trackingNumber") String trackingNumber,
            @Param("currentStatus") InspectionStatus currentStatus
    );

    List<InspectionListResponse> findInspectionList(RowBounds rowBounds,
                                                    @Param("memberId") Long memberId,
                                                    @Param("status") ItemInspectionStatus status);

    int countInspectionList(@Param("memberId") Long memberId,
                            @Param("status") ItemInspectionStatus status);

    InspectionDetailResponse findDetailById(Long inspectionId);

    List<AdminInspectionListResponse> findAllWithFilter(@Param("request") AdminInspectionListRequest request,
                                                        RowBounds rowBounds,
                                                        @Param("order") String sortOrder);

    long countWithFilter(AdminInspectionListRequest request);

    void updateStatus(@Param("inspectionId") Long inspectionId,
                      @Param("newStatus") InspectionStatus newStatus,
                      @Param("currentStatus") InspectionStatus currentStatus,
                      @Param("failureReason") String failureReason);
}
