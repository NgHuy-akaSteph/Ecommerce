package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.AddressRequest;
import com.myapp.ecommerce.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressResponse create(String userId, AddressRequest request);

    AddressResponse update(String addressId, String userId, AddressRequest request);

    void delete(String addressId, String userId);

    List<AddressResponse> getMyAddresses(String userId);

    AddressResponse getById(String addressId, String userId);

    AddressResponse setDefault(String addressId, String userId);
}
