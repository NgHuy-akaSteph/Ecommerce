package com.myapp.ecommerce.controller;


import com.myapp.ecommerce.dto.request.TagRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.TagResponse;
import com.myapp.ecommerce.entity.Tag;
import com.myapp.ecommerce.service.TagService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Tag management")
public class TagController {

    TagService tagService;

    @PostMapping
    ResponseEntity<ApiResponse<TagResponse>> create(@RequestBody @Valid TagRequest tagRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a new tag", this.tagService.create(tagRequest)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<ApiPagination<TagResponse>>> getAllTags(@Filter Specification<Tag> spec, Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all tags", this.tagService.getAll(spec, pageable)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<TagResponse>> getDetails(@PathVariable("id") String tagId) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get details of a tag", this.tagService.getDetails(tagId)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<TagResponse>> update(@PathVariable("id") String tagId, @RequestBody @Valid TagRequest tagRequest) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update a tag by id", this.tagService.update(tagId, tagRequest)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable("id") String tagId) {
        this.tagService.delete(tagId);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete a tag by id", "success"));
    }
}