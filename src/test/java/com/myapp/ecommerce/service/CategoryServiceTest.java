//package com.myapp.ecommerce.service;
//
//import com.myapp.ecommerce.dto.request.CategoryRequest;
//import com.myapp.ecommerce.dto.response.ApiPagination;
//import com.myapp.ecommerce.dto.response.CategoryResponse;
//import com.myapp.ecommerce.entity.Category;
//import com.myapp.ecommerce.entity.Product;
//import com.myapp.ecommerce.exception.AppException;
//import com.myapp.ecommerce.exception.ErrorCode;
//import com.myapp.ecommerce.mapper.CategoryMapper;
//import com.myapp.ecommerce.repository.CategoryRepository;
//import com.myapp.ecommerce.service.impl.CategoryServiceImpl;
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
//class CategoryServiceTest {
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @Mock
//    private CategoryMapper categoryMapper;
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private CategoryServiceImpl categoryService;
//
//    private Category category;
//    private CategoryResponse categoryResponse;
//    private CategoryRequest categoryRequest;
//
//    @BeforeEach
//    void setUp() {
//        category = new Category();
//        category.setId("1");
//        category.setName("Test Category");
//
//        categoryResponse = new CategoryResponse();
//        categoryResponse.setId("1");
//        categoryResponse.setName("Test Category");
//
//        categoryRequest = new CategoryRequest();
//        categoryRequest.setName("Test Category");
//    }
//
//    @Test
//    void createCategory_Success() {
//        // Given
//        when(categoryRepository.existsByName(anyString())).thenReturn(false);
//        when(categoryMapper.toCategory(any(CategoryRequest.class))).thenReturn(category);
//        when(categoryRepository.save(any(Category.class))).thenReturn(category);
//        when(categoryMapper.toCategoryResponse(any(Category.class))).thenReturn(categoryResponse);
//
//        // When
//        CategoryResponse result = categoryService.create(categoryRequest);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(categoryResponse.getId(), result.getId());
//        assertEquals(categoryResponse.getName(), result.getName());
//        verify(categoryRepository, times(1)).save(any(Category.class));
//    }
//
//    @Test
//    void createCategory_NameExists_ThrowsException() {
//        // Given
//        when(categoryRepository.existsByName(anyString())).thenReturn(true);
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> categoryService.create(categoryRequest));
//        assertEquals(ErrorCode.CATEGORY_EXISTED, exception.getErrorCode());
//    }
//
//    @Test
//    void updateCategory_Success() {
//        // Given
//        CategoryRequest updateRequest = new CategoryRequest();
//        updateRequest.setName("Updated Category");
//
//        when(categoryRepository.existsByName(anyString())).thenReturn(false);
//        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
//        when(categoryRepository.save(any(Category.class))).thenReturn(category);
//        when(categoryMapper.toCategoryResponse(any(Category.class))).thenReturn(categoryResponse);
//
//        // When
//        CategoryResponse result = categoryService.update("1", updateRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(categoryRepository, times(1)).save(any(Category.class));
//    }
//
//    @Test
//    void updateCategory_NotFound_ThrowsException() {
//        // Given
//        when(categoryRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> categoryService.update("1", categoryRequest));
//        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void getCategoryDetails_Success() {
//        // Given
//        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
//        when(categoryMapper.toCategoryResponse(any(Category.class))).thenReturn(categoryResponse);
//
//        // When
//        CategoryResponse result = categoryService.getDetails("1");
//
//        // Then
//        assertNotNull(result);
//        assertEquals(categoryResponse.getId(), result.getId());
//    }
//
//    @Test
//    void getAllCategories_Success() {
//        // Given
//        List<Category> categories = Arrays.asList(category);
//        Page<Category> categoryPage = new PageImpl<>(categories);
//        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(categoryPage);
//        when(categoryMapper.toCategoryResponse(any(Category.class))).thenReturn(categoryResponse);
//
//        // When
//        ApiPagination<CategoryResponse> result = categoryService.getAll(mock(Specification.class), mock(Pageable.class));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getResult().size());
//    }
//
//    @Test
//    void deleteCategory_Success() {
//        // Given
//        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
//        doNothing().when(categoryRepository).delete(any(Category.class));
//
//        // When
//        categoryService.delete("1");
//
//        // Then
//        verify(categoryRepository, times(1)).delete(any(Category.class));
//    }
//}