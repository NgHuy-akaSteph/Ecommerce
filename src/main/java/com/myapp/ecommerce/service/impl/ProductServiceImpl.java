package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.ProductCreationRequest;
import com.myapp.ecommerce.dto.request.ProductUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ProductResponse;
import com.myapp.ecommerce.entity.Category;
import com.myapp.ecommerce.entity.OrderDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.Tag;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.ProductMapper;
import com.myapp.ecommerce.repository.CartDetailRepository;
import com.myapp.ecommerce.repository.CategoryRepository;
import com.myapp.ecommerce.repository.OrderDetailRepository;
import com.myapp.ecommerce.repository.ProductRepository;
import com.myapp.ecommerce.repository.TagRepository;
import com.myapp.ecommerce.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    TagRepository tagRepository;
    ProductMapper productMapper;
    CartDetailRepository cartDetailRepository;
    OrderDetailRepository orderDetailRepository;

    @Override
    public ProductResponse create(ProductCreationRequest request) {
        log.info("Creating product with name: {}", request.getName());

        Product product = productMapper.toProduct(request);
        if(request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }
        if(request.getTagsId() != null) {
            List<Tag> tagList = tagRepository.findAllById(request.getTagsId());
            product.setTags(tagList);
        }
        
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(String productId, ProductUpdateRequest request) {
        log.info("Update a product");
        
        Product entityDB = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.updateProduct(entityDB, request);
        if(request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            entityDB.setCategory(category);
        }

        if(request.getTagsId() != null) {
            List<Tag> tagList = tagRepository.findAllById(request.getTagsId());
            entityDB.setTags(tagList);
        }
        return productMapper.toProductResponse(productRepository.save(entityDB));
    }


    @Override
    public ProductResponse getDetails(String productId) {
        log.info("Get product details");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        List<Product> productList = productRepository.findAll();
        return productList.stream().map(productMapper::toProductResponse).toList();
    }

    @Override
    public ApiPagination<ProductResponse> getAll(Specification<Product> spec, Pageable pageable) {
        log.info("Get all products");
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<ProductResponse> productResponseList = productPage.map(productMapper::toProductResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(productPage.getTotalPages());
        mt.setTotal(productPage.getTotalElements());


        return ApiPagination.<ProductResponse>builder()
                .meta(mt)
                .result(productResponseList)
                .build();
    }

    @Override
    public void delete(String productId) {
        log.info("Delete a product");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        cartDetailRepository.deleteAll(product.getCartDetails());
        orderDetailRepository.deleteAll(product.getOrderDetails());
        productRepository.delete(product);
    }

    @Override
    public ApiPagination<ProductResponse> fetchProductsByCategory(String categoryId, Pageable pageable) {
        log.info("Fetch products by category");

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Page<Product> productPage = productRepository.findByCategory(category, pageable);

        List<ProductResponse> productResponseList = productPage.getContent().stream()
                .map(productMapper::toProductResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(productPage.getTotalPages());
        mt.setTotal(productPage.getTotalElements());

        return ApiPagination.<ProductResponse>builder()
                .meta(mt)
                .result(productResponseList)
                .build();
    }

    @Override
    public void save(Product product) {
        //Save in Elasticsearch

        //Save in database
        productRepository.save(product);
    }

    @Override
    public void deleteAll(List<Product> products) {
        products.forEach(product -> {
            List<OrderDetail> orderDetails = product.getOrderDetails();
            orderDetailRepository.deleteAll(orderDetails);
        });
        productRepository.deleteAll(products);
    }

    @Override
    public void deleteAllById(List<String> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        // Delete in Elasticsearch

        // Delete in database
        products.forEach(product -> {
            List<OrderDetail> orderDetails = product.getOrderDetails();
            orderDetailRepository.deleteAll(orderDetails);
        });
        productRepository.deleteAll(products);
    }

}
