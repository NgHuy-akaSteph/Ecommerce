package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.VariantOptionRequest;
import com.myapp.ecommerce.dto.response.VariantOptionResponse;

import java.util.List;
import java.util.UUID;

public interface VariantOptionService {

    VariantOptionResponse create(VariantOptionRequest request);

    VariantOptionResponse update(UUID optionId, VariantOptionRequest request);

    VariantOptionResponse getDetails(UUID optionId);

    List<VariantOptionResponse> getAll();

    void delete(UUID optionId);
}
