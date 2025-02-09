package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.TagRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.TagResponse;
import com.myapp.ecommerce.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface TagService {

    TagResponse create(TagRequest request);

    TagResponse update(String tagId, TagRequest request);

    TagResponse getDetails(String tagId);

    ApiPagination<TagResponse> getAll(Specification<Tag> spec, Pageable pageable);

    void delete(String tagId);

    Tag findByName(String tagName);
}
