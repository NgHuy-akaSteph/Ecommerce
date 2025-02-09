package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.CategoryRequest;
import com.myapp.ecommerce.dto.response.CategoryResponse;
import com.myapp.ecommerce.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toCategoryResponse(Category category);

    Category toCategory(CategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}
