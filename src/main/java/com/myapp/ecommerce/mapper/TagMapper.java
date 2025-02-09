package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.TagRequest;
import com.myapp.ecommerce.dto.response.TagResponse;
import com.myapp.ecommerce.entity.Tag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponse toTagResponse(Tag tag);

    Tag toTag(TagRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTag(@MappingTarget Tag tag, TagRequest request);
}
