package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.enums.ProductStatus;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductBrand;
import com.jcaa.usersmanagement.domain.valueobject.ProductCategoryId;
import com.jcaa.usersmanagement.domain.valueobject.ProductCode;
import com.jcaa.usersmanagement.domain.valueobject.ProductDescription;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import com.jcaa.usersmanagement.domain.valueobject.ProductImageUrl;
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

@DisplayName("InMemoryProductRepositoryAdapter")
class InMemoryProductRepositoryAdapterTest {

  private static final String CATEGORY_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String SUPPLIER_ID = "550e8400-e29b-41d4-a716-446655440101";

  private InMemoryProductRepositoryAdapter repository;
  private ProductModel availableProduct;
  private ProductModel lowStockProduct;

  @BeforeEach
  void setUp() {
    repository = new InMemoryProductRepositoryAdapter();
    availableProduct = product("p-001", "CODE-01", "Car Shampoo", 20, 5, "Shine");
    lowStockProduct = product("p-002", "CODE-02", "Wax", 2, 5, "Shine");
    repository.save(availableProduct);
    repository.save(lowStockProduct);
  }

  @Test
  @DisplayName("saves, retrieves, updates, sorts and deletes products")
  void shouldSupportProductRepositoryOperations() {
    assertEquals(
        availableProduct,
        repository.getById(new ProductId("p-001")).orElseThrow());
    assertEquals(
        lowStockProduct,
        repository.getByCode(new ProductCode("CODE-02")).orElseThrow());
    assertEquals(List.of("Car Shampoo", "Wax"), repository.getAll().stream()
        .map(product -> product.getName().value())
        .toList());

    final ProductModel updated = product("p-001", "CODE-01", "Premium Shampoo", 20, 5, "Shine");
    repository.update(updated);
    assertEquals(updated, repository.getById(new ProductId("p-001")).orElseThrow());

    repository.delete(new ProductId("p-002"));
    assertTrue(repository.getById(new ProductId("p-002")).isEmpty());
  }

  @Test
  @DisplayName("applies case-insensitive filters and returns low-stock products")
  void shouldSearchAndFindLowStockProducts() {
    final List<ProductModel> searchResults =
        repository.search(new SearchProductsQuery("shampoo", null, "available", "SHI"));

    assertEquals(List.of(availableProduct), searchResults);
    assertEquals(List.of(lowStockProduct), repository.getLowStock());
    assertEquals(ProductStatus.LOW_STOCK, repository.getLowStock().get(0).getStatus());
  }

  private static ProductModel product(
      final String id,
      final String code,
      final String name,
      final int stock,
      final int minimumStock,
      final String brand) {
    return ProductModel.create(
        new ProductId(id),
        new ProductCode(code),
        new ProductName(name),
        new ProductBrand(brand),
        new ProductDescription("Product description"),
        new ProductPrice(new BigDecimal("100.00")),
        new ProductPrice(new BigDecimal("120.00")),
        new ProductStock(stock),
        new ProductMinimumStock(minimumStock),
        new ProductCategoryId(CATEGORY_ID),
        Set.of(new ProductSupplierId(SUPPLIER_ID)),
        new ProductImageUrl("https://example.com/product.jpg"));
  }
}
