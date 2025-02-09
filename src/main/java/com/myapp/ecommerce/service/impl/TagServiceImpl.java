package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.TagRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.TagResponse;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.Tag;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.TagMapper;
import com.myapp.ecommerce.repository.TagRepository;
import com.myapp.ecommerce.service.ProductService;
import com.myapp.ecommerce.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TagServiceImpl implements TagService {

    TagRepository tagRepository;
    ProductService productService;
    TagMapper tagMapper;

    @Override
    public TagResponse create(TagRequest request) {
        log.info("Create a tag");

        Tag tag = tagMapper.toTag(request);
        return tagMapper.toTagResponse(tagRepository.save(tag));
    }

    @Override
    public TagResponse update(String tagId, TagRequest request) {
        log.info("Update a tag");

        Tag tagDB = tagRepository.findById(tagId).
                orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        tagMapper.updateTag(tagDB, request);
        return tagMapper.toTagResponse(tagRepository.save(tagDB));
    }

    @Override
    public TagResponse getDetails(String tagId) {
        log.info("Get details of a tag");
        Tag tagDB = tagRepository.findById(tagId).
                orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        return tagMapper.toTagResponse(tagDB);
    }

    @Override
    public ApiPagination<TagResponse> getAll(Specification<Tag> spec, Pageable pageable) {
        log.info("Get all tags");
        Page<Tag> tagPage = tagRepository.findAll(spec, pageable);
        List<TagResponse> tagResponseList = tagPage.getContent()
                .stream().map(tagMapper::toTagResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(tagPage.getTotalPages());
        mt.setTotal(tagPage.getTotalElements());

        return ApiPagination.<TagResponse>builder()
                .meta(mt)
                .result(tagResponseList)
                .build();
    }

    @Override
    public void delete(String tagId) {
        log.info("Delete a tag");
        Tag tag = tagRepository.findById(tagId).
                orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        List<Product> productList = tag.getProducts();
        productList.forEach(product -> {
            product.setTags(null); //note: this is a bug, it should be product.getTags().remove(tag);
            productService.save(product);
        });
        tagRepository.deleteById(tagId);
    }

    @Override
    public Tag findByName(String tagName) {
        return tagRepository.findByName(tagName);
    }
}
