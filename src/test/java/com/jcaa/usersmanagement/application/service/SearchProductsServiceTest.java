package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.SearchProductsPort;
import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas para SearchProductsService – b�squeda por fragmentos de texto y filtros. */
@DisplayName("SearchProductsService")
@ExtendWith(MockitoExtension.class)
class SearchProductsServiceTest {

  @Mock private SearchProductsPort searchProductsPort;

  private SearchProductsService service;

  @BeforeEach
  void setUp() {
    service = new SearchProductsService(searchProductsPort);
  }

  @Test
  @DisplayName("execute() devuelve resultados cuando se proporciona un fragmento de nombre")
  void shouldReturnMatchingProductsByName() {
    final SearchProductsQuery query =
        new SearchProductsQuery("Apple", null, null, null); // solo fragmento de nombre

    final ProductModel appleProduct =
        ProductModel.create(
            new ProductId("p-001"),
            new ProductCode("CODE-01"),
            new ProductName("Apple iPhone"),
            new ProductBrand("Apple"),
            new ProductDescription("Teléfono inteligente"),
            new ProductPrice(new BigDecimal("999.00")),
            new ProductPrice(new BigDecimal("1099.00")),
            new ProductStock(50),
            new ProductMinimumStock(10),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440010"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440110")),
            new ProductImageUrl("http://example.com/iphone.jpg"));

    final ProductModel orangeProduct =
        ProductModel.create(
            new ProductId("p-002"),
            new ProductCode("CODE-02"),
            new ProductName("Naranja Fresca"),
            new ProductBrand("Naturaleza"),
            new ProductDescription("Jugo natural"),
            new ProductPrice(new BigDecimal("15.00")),
            new ProductPrice(new BigDecimal("18.00")),
            new ProductStock(100),
            new ProductMinimumStock(20),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440011"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440111")),
            new ProductImageUrl("http://example.com/naranja.jpg"));

    when(searchProductsPort.search(any())).thenReturn(List.of(appleProduct));

    final List<ProductModel> result = service.execute(query);

    assertAll(
        "búsqueda por nombre",
        () -> assertNotNull(result),
        () -> assertEquals(1, result.size()),
        () -> assertEquals("Apple iPhone", result.get(0).getName().value()),
        () -> assertEquals("Apple", result.get(0).getBrand().toString()));

    verify(searchProductsPort).search(query);
  }

  @Test
  @DisplayName("execute() devuelve todos los productos cuando la consulta es null")
  void shouldReturnAllWhenQueryIsNull() {
    final SearchProductsQuery nullQuery = null;

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

    when(searchProductsPort.search(any())).thenReturn(List.of(product1));

    final List<ProductModel> result = service.execute(nullQuery);

    assertAll(
        "consulta null -> todos los productos",
        () -> assertNotNull(result),
        () -> assertEquals(1, result.size()),
        () -> assertEquals("CODE-01", result.get(0).getCode().value()));

    // El servicio debe convertir null a SearchProductsQuery.all()
    final SearchProductsQuery expectedQuery = SearchProductsQuery.all();
    verify(searchProductsPort).search(expectedQuery);
  }

  @Test
  @DisplayName("execute() filtra por categoría cuando se proporciona categoryId")
  void shouldFilterByCategory() {
    final SearchProductsQuery query =
        new SearchProductsQuery(null, "550e8400-e29b-41d4-a716-446655440010", null, null);

    final ProductModel electronicsProduct =
        ProductModel.create(
            new ProductId("p-001"),
            new ProductCode("CODE-01"),
            new ProductName("Apple iPhone"),
            new ProductBrand("Apple"),
            new ProductDescription("Teléfono inteligente"),
            new ProductPrice(new BigDecimal("999.00")),
            new ProductPrice(new BigDecimal("1099.00")),
            new ProductStock(50),
            new ProductMinimumStock(10),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440010"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440110")),
            new ProductImageUrl("http://example.com/iphone.jpg"));

    when(searchProductsPort.search(any())).thenReturn(List.of(electronicsProduct));

    final List<ProductModel> result = service.execute(query);

    assertAll(
        "filtro por categoría",
        () -> assertNotNull(result),
        () -> assertEquals(1, result.size()),
        () -> assertEquals("550e8400-e29b-41d4-a716-446655440010", result.get(0).getCategoryId().value()));

    verify(searchProductsPort).search(query);
  }

  @Test
  @DisplayName("execute() filtra por estado cuando se proporciona status")
  void shouldFilterByStatus() {
    final SearchProductsQuery query =
        new SearchProductsQuery(null, null, "AVAILABLE", null);

    final ProductModel availableProduct =
        ProductModel.create(
            new ProductId("p-001"),
            new ProductCode("CODE-01"),
            new ProductName("Producto Disponible"),
            new ProductBrand("Marca X"),
            new ProductDescription("Description"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440101")),
            new ProductImageUrl("http://example.com/img.jpg"));

    when(searchProductsPort.search(any())).thenReturn(List.of(availableProduct));

    final List<ProductModel> result = service.execute(query);

    assertAll(
        "filtro por estado",
        () -> assertNotNull(result),
        () -> assertEquals(1, result.size()),
        () -> assertEquals("AVAILABLE", result.get(0).getStatus().name()));

    verify(searchProductsPort).search(query);
  }

  @Test
  @DisplayName("execute() devuelve lista vacía cuando no hay coincidencias")
  void shouldReturnEmptyWhenNoMatches() {
    final SearchProductsQuery query =
        new SearchProductsQuery("NonExistentName", null, null, null);

    when(searchProductsPort.search(any())).thenReturn(List.of());

    final List<ProductModel> result = service.execute(query);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(searchProductsPort).search(query);
  }
}