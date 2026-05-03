package com.biddingmate.biddinggo.address.service;

import com.biddingmate.biddinggo.address.dto.AddressListResponse;
import com.biddingmate.biddinggo.address.dto.CreateAddressRequest;
import com.biddingmate.biddinggo.address.dto.CreateAddressResponse;

import java.util.List;

public interface AddressService {
    CreateAddressResponse createAddress(CreateAddressRequest request, Long memberId);
    List<AddressListResponse> findAllAddress(Long memberId);
    void updateDefaultAddress(Long addressId, Long memberId);
    void deleteAddress(Long addressId, Long memberId);
}
