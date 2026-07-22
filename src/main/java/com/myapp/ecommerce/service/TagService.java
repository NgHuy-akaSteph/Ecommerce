package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.TagRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.TagResponse;
import com.myapp.ecommerce.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface TagService {

    TagResponse create(TagRequest request);

    TagResponse update(UUID tagId, TagRequest request);

    TagResponse getDetails(UUID tagId);

    ApiPagination<TagResponse> getAll(Specification<Tag> spec, Pageable pageable);

    void delete(UUID tagId);

    Tag findByName(String tagName);
}
