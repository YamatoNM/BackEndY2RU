package md.impact.ecommerce;

import md.impact.ecommerce.repositories.CategoryRepository;
import md.impact.ecommerce.repositories.ProductRepository;
import md.impact.ecommerce.services.DiscountService;
import md.impact.ecommerce.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock DiscountService discountService;
    @Mock CategoryRepository categoryRepository;

    ProductService productService;

    @BeforeEach
    void setUp(){
        productService = new ProductService(productRepository,
                discountService);
    }
    @Test
    void list_a_category_filters_at_the_database_not_in_java(){
        when(productRepository.findByCategoryIdOrderByIdAsc(2L))
                .thenReturn(List.of());

        productService.list(2L);

        verify(productRepository).findByCategoryIdOrderByIdAsc(2L);
        verify(productRepository, never()).findAllByOrderByIdAsc();

    }



}
