//package com.myapp.ecommerce.service;
//
//import com.myapp.ecommerce.dto.request.ProductCreationRequest;
//import com.myapp.ecommerce.dto.request.ProductUpdateRequest;
//import com.myapp.ecommerce.dto.response.ApiPagination;
//import com.myapp.ecommerce.dto.response.ProductResponse;
//import com.myapp.ecommerce.entity.Category;
//import com.myapp.ecommerce.entity.Product;
//import com.myapp.ecommerce.entity.Tag;
//import com.myapp.ecommerce.exception.AppException;
//import com.myapp.ecommerce.exception.ErrorCode;
//import com.myapp.ecommerce.mapper.ProductMapper;
//import com.myapp.ecommerce.repository.*;
//import com.myapp.ecommerce.service.impl.ProductServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @Mock
//    private TagRepository tagRepository;
//
//    @Mock
//    private CartDetailRepository cartDetailRepository;
//
//    @Mock
//    private OrderDetailRepository orderDetailRepository;
//
//    @Mock
//    private ProductMapper productMapper;
//
//    @InjectMocks
//    private ProductServiceImpl productService;
//
//    private Product product;
//    private ProductResponse productResponse;
//    private ProductCreationRequest productCreationRequest;
//    private ProductUpdateRequest productUpdateRequest;
//    private Category category;
//    private Tag tag;
//
//    @BeforeEach
//    void setUp() {
//        category = new Category();
//        category.setId("1");
//        category.setName("Test Category");
//
//        tag = new Tag();
//        tag.setId("1");
//        tag.setName("Test Tag");
//
//        product = new Product();
//        product.setId("1");
//        product.setName("Test Product");
//        product.setPrice(100.0);
//        product.setCategory(category);
//        product.setTags(Arrays.asList(tag));
//
//        productResponse = new ProductResponse();
//        productResponse.setId("1");
//        productResponse.setName("Test Product");
//        productResponse.setPrice(100.0);
//
//        productCreationRequest = new ProductCreationRequest();
//        productCreationRequest.setName("Test Product");
//        productCreationRequest.setPrice(100.0);
//        productCreationRequest.setCategoryId("1");
//        productCreationRequest.setTagIds(Arrays.asList("1"));
//
//        productUpdateRequest = new ProductUpdateRequest();
//        productUpdateRequest.setName("Updated Product");
//        productUpdateRequest.setPrice(200.0);
//    }
//
//    @Test
//    void createProduct_Success() {
//        // Given
//        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
//        when(tagRepository.findAllById(anyList())).thenReturn(Arrays.asList(tag));
//        when(productMapper.toProduct(any(ProductCreationRequest.class))).thenReturn(product);
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//        when(productMapper.toProductResponse(any(Product.class))).thenReturn(productResponse);
//
//        // When
//        ProductResponse result = productService.create(productCreationRequest);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(productResponse.getId(), result.getId());
//        assertEquals(productResponse.getName(), result.getName());
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void createProduct_CategoryNotFound_ThrowsException() {
//        // Given
//        when(categoryRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> productService.create(productCreationRequest));
//        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void updateProduct_Success() {
//        // Given
//        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//        when(productMapper.toProductResponse(any(Product.class))).thenReturn(productResponse);
//
//        // When
//        ProductResponse result = productService.update("1", productUpdateRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void updateProduct_NotFound_ThrowsException() {
//        // Given
//        when(productRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> productService.update("1", productUpdateRequest));
//        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void getProductDetails_Success() {
//        // Given
//        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));
//        when(productMapper.toProductResponse(any(Product.class))).thenReturn(productResponse);
//
//        // When
//        ProductResponse result = productService.getDetails("1");
//
//        // Then
//        assertNotNull(result);
//        assertEquals(productResponse.getId(), result.getId());
//    }
//
//    @Test
//    void getAllProducts_Success() {
//        // Given
//        List<Product> products = Arrays.asList(product);
//        Page<Product> productPage = new PageImpl<>(products);
//        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);
//        when(productMapper.toProductResponse(any(Product.class))).thenReturn(productResponse);
//
//        // When
//        ApiPagination<ProductResponse> result = productService.getAll(mock(Specification.class), mock(Pageable.class));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getResult().size());
//    }
//
//    @Test
//    void getProductsByCategory_Success() {
//        // Given
//        List<Product> products = Arrays.asList(product);
//        Page<Product> productPage = new PageImpl<>(products);
//        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
//        when(productRepository.findByCategory(any(Category.class), any(Pageable.class))).thenReturn(productPage);
//        when(productMapper.toProductResponse(any(Product.class))).thenReturn(productResponse);
//
//        // When
//        ApiPagination<ProductResponse> result = productService.fetchProductsByCategory("1", mock(Pageable.class));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getResult().size());
//    }
//
//    @Test
//    void deleteProduct_Success() {
//        // Given
//        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));
//        doNothing().when(productRepository).delete(any(Product.class));
//
//        // When
//        productService.delete("1");
//
//        // Then
//        verify(productRepository, times(1)).delete(any(Product.class));
//    }
//
//    @Test
//    void deleteAllProducts_Success() {
//        // Given
//        List<Product> products = Arrays.asList(product);
//        doNothing().when(productRepository).deleteAll(anyList());
//
//        // When
//        productService.deleteAll(products);
//
//        // Then
//        verify(productRepository, times(1)).deleteAll(anyList());
//    }
//}