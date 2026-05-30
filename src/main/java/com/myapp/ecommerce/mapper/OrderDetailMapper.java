package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.OrderDetailRequest;
import com.myapp.ecommerce.dto.response.OrderDetailResponse;
import com.myapp.ecommerce.entity.OrderDetail;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    OrderDetail toOrderDetail(OrderDetailRequest orderDetailRequest);

    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderDetail(@MappingTarget OrderDetail orderDetail, OrderDetailRequest orderDetailRequest);
}
