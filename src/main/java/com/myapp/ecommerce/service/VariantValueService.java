package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.VariantValueRequest;
import com.myapp.ecommerce.dto.response.VariantValueResponse;

import java.util.List;
import java.util.UUID;

public interface VariantValueService {

    VariantValueResponse create(VariantValueRequest request);

    VariantValueResponse update(UUID valueId, VariantValueRequest request);

    VariantValueResponse getDetails(UUID valueId);

    List<VariantValueResponse> getAll();

    List<VariantValueResponse> getByOptionId(UUID optionId);

    void delete(UUID valueId);
}
