package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.response.CartDetailResponse;
import com.myapp.ecommerce.entity.CartDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartDetailMapper {
    CartDetailResponse toCartDetailResponse(CartDetail request);
}
