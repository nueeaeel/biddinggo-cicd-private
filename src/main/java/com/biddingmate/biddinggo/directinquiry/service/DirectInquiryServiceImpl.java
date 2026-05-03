package com.biddingmate.biddinggo.directinquiry.service;

import com.biddingmate.biddinggo.directinquiry.dto.DirectInquiryView;
import com.biddingmate.biddinggo.directinquiry.dto.AnswerDirectInquiryRequest;
import com.biddingmate.biddinggo.directinquiry.dto.AnswerDirectInquiryResponse;
import com.biddingmate.biddinggo.directinquiry.dto.CreateDirectInquiryRequest;
import com.biddingmate.biddinggo.directinquiry.dto.CreateDirectInquiryResponse;
import com.biddingmate.biddinggo.directinquiry.mapper.DirectInquiryMapper;
import com.biddingmate.biddinggo.directinquiry.model.DirectInquiry;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.notification.model.NotificationType;
import com.biddingmate.biddinggo.notification.service.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectInquiryServiceImpl implements DirectInquiryService {

    private final DirectInquiryMapper directInquiryMapper;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public CreateDirectInquiryResponse createDirectInquiry(CreateDirectInquiryRequest request, Long memberId) {
        DirectInquiry directInquiry = DirectInquiry.builder()
                .writerId(memberId)
                .category(request.getCategory())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        int insert = directInquiryMapper.insert(directInquiry);

        if (insert <= 0) {
            throw new CustomException(ErrorType.ADMIN_INQUIRY_CREATED_FAIL);
        }

        notificationPublisher.publishToActiveAdmins(
                NotificationType.DIRECT_INQUIRY,
                "새로운 1:1 문의가 등록되었습니다. 문의 #" + directInquiry.getId(),
                "/admins/direct-inquiries"
        );



        return CreateDirectInquiryResponse.builder()
                .id(directInquiry.getId())
                .writerId(directInquiry.getWriterId())
                .category(directInquiry.getCategory())
                .content(directInquiry.getContent())
                .createdAt(directInquiry.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DirectInquiryView> findAllDirectInquiry(BasePageRequest request) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }
        String sortOrder = order.toUpperCase();

        List<DirectInquiryView> list;
        int count;

        list = directInquiryMapper.findAllDirectInquiry(rowBounds, sortOrder);
        count = directInquiryMapper.getDirectInquiryTotal();

        return PageResponse.of(list, request.getPage(), request.getSize(), count);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DirectInquiryView> findDirectInquiry(BasePageRequest request, Long memberId) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }
        String sortOrder = order.toUpperCase();

        List<DirectInquiryView> list;
        int count;
      
        list = directInquiryMapper.findDirectInquiryOfMe(rowBounds, memberId, sortOrder);
        count = directInquiryMapper.getDirectInquiryTotalOfMe(memberId);
        
        return PageResponse.of(list, request.getPage(), request.getSize(), count);
    }

    @Transactional
    public AnswerDirectInquiryResponse answerDirectInquiry(Long inquiryId, AnswerDirectInquiryRequest request, Long adminId) {
        DirectInquiry directInquiry = directInquiryMapper.findById(inquiryId);

        if (directInquiry == null) {
            throw new CustomException(ErrorType.ADMIN_INQUIRY_NOT_FOUND);
        }

        if (directInquiry.getAnsweredAt() != null) {
            throw new CustomException(ErrorType.ADMIN_INQUIRY_ALREADY_ANSWERED);
        }

        LocalDateTime now = LocalDateTime.now();
        DirectInquiry updateDto = DirectInquiry.builder()
                .id(inquiryId)
                .adminId(adminId)
                .answer(request.getAnswer())
                .answeredAt(now)
                .build();

        int updatedRows = directInquiryMapper.update(updateDto);

        if (updatedRows <= 0) {
            throw new CustomException(ErrorType.ADMIN_INQUIRY_UPDATED_FAIL);
        }

        return AnswerDirectInquiryResponse.builder()
                .id(inquiryId)
                .adminId(adminId)
                .answer(request.getAnswer())
                .answeredAt(now)
                .build();
    }
}
