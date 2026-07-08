package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.VariantValueRequest;
import com.myapp.ecommerce.dto.response.VariantValueResponse;

import java.util.List;

public interface VariantValueService {

    VariantValueResponse create(VariantValueRequest request);

    VariantValueResponse update(String valueId, VariantValueRequest request);

    VariantValueResponse getDetails(String valueId);

    List<VariantValueResponse> getAll();

    List<VariantValueResponse> getByOptionId(String optionId);

    void delete(String valueId);
}
