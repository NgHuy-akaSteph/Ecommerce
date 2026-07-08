package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.VariantOptionRequest;
import com.myapp.ecommerce.dto.response.VariantOptionResponse;
import com.myapp.ecommerce.entity.VariantOption;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.VariantOptionMapper;
import com.myapp.ecommerce.repository.VariantOptionRepository;
import com.myapp.ecommerce.service.VariantOptionService;
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
public class VariantOptionServiceImpl implements VariantOptionService {

    VariantOptionRepository variantOptionRepository;
    VariantOptionMapper variantOptionMapper;

    @Override
    @Transactional
    public VariantOptionResponse create(VariantOptionRequest request) {
        log.info("Create variant option: {}", request.getCode());

        if (variantOptionRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.VARIANT_OPTION_EXISTED);
        }

        VariantOption option = variantOptionMapper.toEntity(request);
        return variantOptionMapper.toResponse(variantOptionRepository.save(option));
    }

    @Override
    @Transactional
    public VariantOptionResponse update(String optionId, VariantOptionRequest request) {
        log.info("Update variant option: {}", optionId);

        VariantOption option = variantOptionRepository.findById(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_OPTION_NOT_FOUND));

        if (!option.getCode().equals(request.getCode())
                && variantOptionRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.VARIANT_OPTION_EXISTED);
        }

        variantOptionMapper.updateEntity(option, request);
        return variantOptionMapper.toResponse(variantOptionRepository.save(option));
    }

    @Override
    @Transactional(readOnly = true)
    public VariantOptionResponse getDetails(String optionId) {
        VariantOption option = variantOptionRepository.findById(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_OPTION_NOT_FOUND));
        return variantOptionMapper.toResponse(option);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantOptionResponse> getAll() {
        return variantOptionRepository.findAll().stream()
                .map(variantOptionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String optionId) {
        log.info("Delete variant option: {}", optionId);
        variantOptionRepository.findById(optionId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_OPTION_NOT_FOUND));
        variantOptionRepository.deleteById(optionId);
    }
}
