package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.VariantOptionRequest;
import com.myapp.ecommerce.dto.response.VariantOptionResponse;

import java.util.List;

public interface VariantOptionService {

    VariantOptionResponse create(VariantOptionRequest request);

    VariantOptionResponse update(String optionId, VariantOptionRequest request);

    VariantOptionResponse getDetails(String optionId);

    List<VariantOptionResponse> getAll();

    void delete(String optionId);
}
