package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jcaa.usersmanagement.application.service.dto.command.CreateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
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
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateProductRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.ProductResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateProductRequest;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductDesktopMapper")
class ProductDesktopMapperTest {

  private static final String CATEGORY_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String SUPPLIER_ID = "550e8400-e29b-41d4-a716-446655440101";

  @Test
  @DisplayName("maps desktop create and update requests to application commands")
  void shouldMapRequestsToCommands() {
    final CreateProductRequest createRequest =
        new CreateProductRequest(
            "CODE-01",
            "Product A",
            "Brand A",
            "Product description",
            new BigDecimal("100.00"),
            new BigDecimal("120.00"),
            10,
            5,
            CATEGORY_ID,
            Set.of(SUPPLIER_ID),
            null);
    final CreateProductCommand createCommand = ProductDesktopMapper.toCreateCommand(createRequest);

    final UpdateProductRequest updateRequest =
        new UpdateProductRequest(
            "p-001",
            "Updated Product",
            "Brand B",
            "Updated description",
            new BigDecimal("110.00"),
            new BigDecimal("130.00"),
            3,
            CATEGORY_ID,
            Set.of(SUPPLIER_ID),
            null);
    final UpdateProductCommand updateCommand = ProductDesktopMapper.toUpdateCommand(updateRequest);

    assertAll(
        () -> assertEquals("CODE-01", createCommand.code()),
        () -> assertEquals(10, createCommand.stock()),
        () -> assertEquals(CATEGORY_ID, createCommand.categoryId()),
        () -> assertEquals("p-001", updateCommand.id()),
        () -> assertEquals(3, updateCommand.minimumStock()),
        () -> assertEquals(Set.of(SUPPLIER_ID), updateCommand.supplierIds()));
  }

  @Test
  @DisplayName("maps domain products to response data and query inputs")
  void shouldMapProductAndSearchQuery() {
    final ProductModel product =
        ProductModel.create(
            new ProductId("p-001"),
            new ProductCode("CODE-01"),
            new ProductName("Product A"),
            new ProductBrand("Brand A"),
            new ProductDescription("Product description"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(2),
            new ProductMinimumStock(5),
            new ProductCategoryId(CATEGORY_ID),
            Set.of(new ProductSupplierId(SUPPLIER_ID)),
            new ProductImageUrl("https://example.com/product.jpg"));

    final ProductResponse response = ProductDesktopMapper.toResponse(product);
    final SearchProductsQuery query =
        ProductDesktopMapper.toSearchQuery("Product", CATEGORY_ID, "LOW_STOCK", "Brand");

    assertAll(
        () -> assertEquals("p-001", response.id()),
        () -> assertEquals("CODE-01", response.code()),
        () -> assertEquals("LOW_STOCK", response.status()),
        () -> assertEquals(true, response.lowStock()),
        () -> assertEquals(Set.of(SUPPLIER_ID), response.supplierIds()),
        () -> assertEquals("Product", query.nameFragment()),
        () -> assertEquals(CATEGORY_ID, query.categoryId()),
        () -> assertEquals("LOW_STOCK", query.status()),
        () -> assertEquals("Brand", query.brandFragment()));
  }
}
