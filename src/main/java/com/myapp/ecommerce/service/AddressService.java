package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.AddressRequest;
import com.myapp.ecommerce.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressResponse create(UUID userId, AddressRequest request);

    AddressResponse update(UUID addressId, UUID userId, AddressRequest request);

    void delete(UUID addressId, UUID userId);

    List<AddressResponse> getMyAddresses(UUID userId);

    AddressResponse getById(UUID addressId, UUID userId);

    AddressResponse setDefault(UUID addressId, UUID userId);
}
