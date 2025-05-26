package com.myapp.ecommerce.controller;


import com.myapp.ecommerce.dto.request.OrderDetailRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiString;
import com.myapp.ecommerce.dto.response.OrderDetailResponse;
import com.myapp.ecommerce.entity.OrderDetail;
import com.myapp.ecommerce.service.OrderDetailService;
import com.myapp.ecommerce.util.annotation.ApiMessage;
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
@RequestMapping("/orderdetails")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailController {

    OrderDetailService orderDetailService;

    @PostMapping
    @ApiMessage("Create order detail successfully")
    ResponseEntity<OrderDetailResponse> create(@RequestBody @Valid OrderDetailRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all order details successfully")
    ResponseEntity<ApiPagination<OrderDetailResponse>> getAll(@Filter Specification<OrderDetail> spec,
                                                              Pageable pageable){
        return ResponseEntity.ok().body(orderDetailService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get a order detail successfully")
    ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable("id") String id){
        return ResponseEntity.ok().body(orderDetailService.getById(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a order detail successfully")
    ResponseEntity<OrderDetailResponse> update(@PathVariable("id") String id,
                                               @RequestBody @Valid OrderDetailRequest request){
        return ResponseEntity.ok().body(orderDetailService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a order detail successfully")
    ResponseEntity<ApiString> delete(@PathVariable("id") String id){
        orderDetailService.delete(id);
        return ResponseEntity.ok().body(new ApiString("success"));
    }
}
