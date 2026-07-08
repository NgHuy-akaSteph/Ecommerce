package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.VariantValueRequest;
import com.myapp.ecommerce.dto.response.VariantValueResponse;
import com.myapp.ecommerce.entity.VariantOption;
import com.myapp.ecommerce.entity.VariantValue;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.VariantValueMapper;
import com.myapp.ecommerce.repository.VariantOptionRepository;
import com.myapp.ecommerce.repository.VariantValueRepository;
import com.myapp.ecommerce.service.VariantValueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VariantValueServiceImpl implements VariantValueService {

    VariantValueRepository variantValueRepository;
    VariantOptionRepository variantOptionRepository;
    VariantValueMapper variantValueMapper;

    @Override
    @Transactional
    public VariantValueResponse create(VariantValueRequest request) {
        log.info("Create variant value: {} for option {}", request.getValue(), request.getOptionId());

        VariantOption option = variantOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_OPTION_NOT_FOUND));

        VariantValue variantValue = variantValueMapper.toEntity(request);
        variantValue.setOption(option);
        return variantValueMapper.toResponse(variantValueRepository.save(variantValue));
    }

    @Override
    @Transactional
    public VariantValueResponse update(String valueId, VariantValueRequest request) {
        log.info("Update variant value: {}", valueId);

        VariantValue value = variantValueRepository.findById(valueId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_VALUE_NOT_FOUND));

        VariantOption option = variantOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_OPTION_NOT_FOUND));

        value.setOption(option);
        variantValueMapper.updateEntity(value, request);
        return variantValueMapper.toResponse(variantValueRepository.save(value));
    }

    @Override
    @Transactional(readOnly = true)
    public VariantValueResponse getDetails(String valueId) {
        VariantValue value = variantValueRepository.findById(valueId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_VALUE_NOT_FOUND));
        return variantValueMapper.toResponse(value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantValueResponse> getAll() {
        return variantValueRepository.findAll().stream()
                .map(variantValueMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantValueResponse> getByOptionId(String optionId) {
        return variantValueRepository.findByOptionId(optionId).stream()
                .map(variantValueMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String valueId) {
        log.info("Delete variant value: {}", valueId);
        variantValueRepository.findById(valueId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_VALUE_NOT_FOUND));
        variantValueRepository.deleteById(valueId);
    }
}
