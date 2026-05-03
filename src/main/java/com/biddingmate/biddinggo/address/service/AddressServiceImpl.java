package com.biddingmate.biddinggo.address.service;

import com.biddingmate.biddinggo.address.dto.AddressListResponse;
import com.biddingmate.biddinggo.address.dto.CreateAddressRequest;
import com.biddingmate.biddinggo.address.dto.CreateAddressResponse;
import com.biddingmate.biddinggo.address.mapper.AddressMapper;
import com.biddingmate.biddinggo.address.model.Address;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public CreateAddressResponse createAddress(CreateAddressRequest request, Long memberId) {
        int count = addressMapper.countByMemberId(memberId);

        if (count >= 3) {
            throw new CustomException(ErrorType.ADDRESS_MAX_COUNT_EXCEEDED);
        }


        Address address = Address.builder()
                .memberId(memberId)
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .createdAt(LocalDateTime.now())
                .build();

        int insert = addressMapper.insert(address);

        if (insert <= 0) {
            throw new CustomException(ErrorType.ADDRESS_CREATED_FAIL);
        }

        return CreateAddressResponse.builder()
                .id(address.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressListResponse> findAllAddress(Long memberId) {
        return addressMapper.findAll(memberId);
    }

    @Override
    @Transactional
    public void updateDefaultAddress(Long addressId, Long memberId) {
        Address address = addressMapper.findById(addressId);

        if (address == null) {
            throw new CustomException(ErrorType.ADDRESS_NOT_FOUND);
        }


        // 기존의 기본 배송지를 해제하며 선택한 배송지 기본 설정
        int update = addressMapper.updateDefault(addressId, memberId);

        if (update <= 0) {
            throw new CustomException(ErrorType.ADDRESS_UPDATE_DEFAULT_FAIL);
        }
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long memberId) {
        Address address = addressMapper.findById(addressId);

        if (address == null) {
            throw new CustomException(ErrorType.ADDRESS_NOT_FOUND);
        }

        int delete = addressMapper.delete(address.getId(), memberId);

        if (delete <= 0) {
            throw new CustomException(ErrorType.ADDRESS_DELETE_DEFAULT_FAIL);
        }
    }
}
