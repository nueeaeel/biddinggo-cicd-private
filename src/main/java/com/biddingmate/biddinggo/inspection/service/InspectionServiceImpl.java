package com.biddingmate.biddinggo.inspection.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.inspection.dto.CreateInspectionRequest;
import com.biddingmate.biddinggo.inspection.dto.InspectionProcessRequest;
import com.biddingmate.biddinggo.inspection.dto.UpdateInspectionShippingRequest;
import com.biddingmate.biddinggo.inspection.mapper.InspectionMapper;
import com.biddingmate.biddinggo.inspection.model.Inspection;
import com.biddingmate.biddinggo.inspection.model.InspectionStatus;
import com.biddingmate.biddinggo.item.mapper.AuctionItemMapper;
import com.biddingmate.biddinggo.item.model.AuctionItem;
import com.biddingmate.biddinggo.item.model.ItemInspectionStatus;
import com.biddingmate.biddinggo.notification.model.NotificationType;
import com.biddingmate.biddinggo.notification.service.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {
    private final InspectionMapper inspectionMapper;
    private final AuctionItemMapper auctionItemMapper;
    private final NotificationPublisher notificationPublisher;

    @Override
    public Long createInspection(CreateInspectionRequest request, Long itemId) {
        if (itemId == null) {
            throw new CustomException(ErrorType.INVALID_INSPECTION_CREATE_REQUEST);
        }

        CreateInspectionRequest.Inspection inspectionRequest = request.getInspection();

        Inspection inspection = Inspection.builder()
                .itemId(itemId)
                .status(InspectionStatus.PENDING)
                .carrier(inspectionRequest != null ? inspectionRequest.getCarrier() : null)
                .trackingNumber(inspectionRequest != null ? inspectionRequest.getTrackingNumber() : null)
                .createdAt(LocalDateTime.now())
                .build();

        int inspectionInsertCount = inspectionMapper.insert(inspection);

        if (inspectionInsertCount != 1 || inspection.getId() == null) {
            throw new CustomException(ErrorType.INSPECTION_SAVE_FAILED);
        }

        return inspection.getId();
    }

    @Override
    public void updateShippingInfo(Long inspectionId, UpdateInspectionShippingRequest request) {
        Inspection inspection = inspectionMapper.findById(inspectionId);

        if (inspection == null) {
            throw new CustomException(ErrorType.INSPECTION_NOT_FOUND);
        }

        if (inspection.getStatus() != InspectionStatus.PENDING) {
            throw new CustomException(ErrorType.INVALID_INSPECTION_STATUS);
        }

        if (inspection.getCarrier() != null || inspection.getTrackingNumber() != null) {
            throw new CustomException(ErrorType.INSPECTION_SHIPPING_INFO_ALREADY_EXISTS);
        }

        int updatedCount = inspectionMapper.updateShippingInfo(
                inspectionId,
                request.getCarrier(),
                request.getTrackingNumber(),
                InspectionStatus.PENDING
        );

        if (updatedCount != 1) {
            throw new CustomException(ErrorType.INSPECTION_SHIPPING_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void processInspection(Long inspectionId, InspectionProcessRequest request) {
        Inspection inspection = inspectionMapper.findById(inspectionId);
        if (inspection == null) {
            throw new CustomException(ErrorType.INSPECTION_NOT_FOUND);
        }

        if (inspection.getStatus() != InspectionStatus.PENDING) {
            throw new CustomException(ErrorType.ALREADY_INSPECTION_STATUS);
        }

        if (!StringUtils.hasText(inspection.getCarrier()) || !StringUtils.hasText(inspection.getTrackingNumber())) {
            throw new CustomException(ErrorType.INSPECTION_SHIPPING_INFO_REQUIRED);
        }

        boolean approved = request.getApproved();

        InspectionStatus status = approved
                ? InspectionStatus.PASSED
                : InspectionStatus.FAILED;

        inspectionMapper.updateStatus(inspectionId, status, InspectionStatus.PENDING, request.getFailureReason());

        Long auctionItemId = inspection.getItemId();

        ItemInspectionStatus itemStatus = approved
                ? ItemInspectionStatus.PASSED
                : ItemInspectionStatus.FAILED;

        auctionItemMapper.updateInspectionStatus(
                auctionItemId,
                itemStatus,
                ItemInspectionStatus.PENDING,
                request.getQuality()
        );

        AuctionItem auctionItem = auctionItemMapper.findById(auctionItemId);
        if (auctionItem != null && auctionItem.getSellerId() != null) {
            String itemName = StringUtils.hasText(auctionItem.getName())
                    ? auctionItem.getName()
                    : "검수 물품";

            String notificationContent = approved
                    ? "[" + itemName + "] 검수가 승인되었습니다."
                    : StringUtils.hasText(request.getFailureReason())
                    ? "[" + itemName + "] 검수가 반려되었습니다. 사유: " + request.getFailureReason()
                    : "[" + itemName + "] 검수가 반려되었습니다.";

            notificationPublisher.publishNotification(
                    auctionItem.getSellerId(),
                    NotificationType.INSPECTION,
                    notificationContent,
                    "/inspections/" + inspectionId
            );
        }
    }
}
