package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetAllProductsPort;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductBrand;
import com.jcaa.usersmanagement.domain.valueobject.ProductCategoryId;
import com.jcaa.usersmanagement.domain.valueobject.ProductCode;
import com.jcaa.usersmanagement.domain.valueobject.ProductDescription;
import com.jcaa.usersmanagement.domain.valueobject.ProductImageUrl;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import com.jcaa.usersmanagement.domain.valueobject.ProductMinimumStock;
import com.jcaa.usersmanagement.domain.valueobject.ProductName;
import com.jcaa.usersmanagement.domain.valueobject.ProductPrice;
import com.jcaa.usersmanagement.domain.valueobject.ProductStock;
import com.jcaa.usersmanagement.domain.valueobject.ProductSupplierId;
import java.util.Set;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas para GetAllProductsService – lista simple del cat�logo. */
@DisplayName("GetAllProductsService")
@ExtendWith(MockitoExtension.class)
class GetAllProductsServiceTest {

  @Mock private GetAllProductsPort getAllProductsPort;

  @InjectMocks private GetAllProductsService service;

  @Test
  @DisplayName("execute() devuelve todos los productos desde el puerto")
  void shouldReturnAllProducts() {
    final ProductModel product1 =
        ProductModel.create(
            new ProductId("p-001"),
            new ProductCode("CODE-01"),
            new ProductName("Producto A"),
            new ProductBrand("Marca X"),
            new ProductDescription("Description A"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440101")),
            new ProductImageUrl("http://example.com/img1.jpg"));

    final ProductModel product2 =
        ProductModel.create(
            new ProductId("p-002"),
            new ProductCode("CODE-02"),
            new ProductName("Producto B"),
            new ProductBrand("Marca Y"),
            new ProductDescription("Description B"),
            new ProductPrice(new BigDecimal("150.00")),
            new ProductPrice(new BigDecimal("180.00")),
            new ProductStock(5),
            new ProductMinimumStock(2),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440002"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440102")),
            new ProductImageUrl("http://example.com/img2.jpg"));

    final List<ProductModel> expectedList = List.of(product1, product2);
    when(getAllProductsPort.getAll()).thenReturn(expectedList);

    final List<ProductModel> result = service.execute();

    assertAll(
        "listado completo",
        () -> assertNotNull(result),
        () -> assertEquals(2, result.size()),
        () -> assertEquals("CODE-01", result.get(0).getCode().value()),
        () -> assertEquals("CODE-02", result.get(1).getCode().value()));

    verify(getAllProductsPort).getAll();
  }

  @Test
  @DisplayName("execute() devuelve lista vac�a cuando no hay productos")
  void shouldReturnEmptyListWhenNoProducts() {
    when(getAllProductsPort.getAll()).thenReturn(List.of());

    final List<ProductModel> result = service.execute();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(getAllProductsPort).getAll();
  }
}