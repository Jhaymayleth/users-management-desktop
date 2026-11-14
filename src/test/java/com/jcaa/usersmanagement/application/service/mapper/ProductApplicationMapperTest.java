package com.jcaa.usersmanagement.application.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.jcaa.usersmanagement.application.service.dto.command.CreateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
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
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductApplicationMapper")
class ProductApplicationMapperTest {

  private static final String CATEGORY_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String SUPPLIER_ID = "550e8400-e29b-41d4-a716-446655440101";

  @Test
  @DisplayName("maps a create command to a domain product")
  void shouldMapCreateCommand() {
    final CreateProductCommand command =
        new CreateProductCommand(
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

    final ProductModel product = ProductApplicationMapper.fromCreateCommandToModel(command);

    assertAll(
        () -> assertNotNull(product.getId()),
        () -> assertEquals("CODE-01", product.getCode().value()),
        () -> assertEquals("Product A", product.getName().value()),
        () -> assertEquals(CATEGORY_ID, product.getCategoryId().value()),
        () -> assertEquals(Set.of(new ProductSupplierId(SUPPLIER_ID)), product.getSupplierIds()),
        () -> assertEquals("", product.getImageUrl().toString()));
  }

  @Test
  @DisplayName("preserves current optional values when update command omits them")
  void shouldPreserveOptionalValuesOnUpdate() {
    final ProductImageUrl imageUrl = new ProductImageUrl("https://example.com/product.jpg");
    final ProductModel current = product(imageUrl, Set.of(new ProductSupplierId(SUPPLIER_ID)));
    final UpdateProductCommand command =
        new UpdateProductCommand(
            current.getId().value(),
            "Updated Product",
            "Updated Brand",
            "Updated description",
            new BigDecimal("110.00"),
            new BigDecimal("130.00"),
            4,
            CATEGORY_ID,
            Set.of(),
            null);

    final ProductModel updated =
        ProductApplicationMapper.fromUpdateCommandToModel(command, current);

    assertAll(
        () -> assertEquals(imageUrl, updated.getImageUrl()),
        () -> assertEquals(current.getSupplierIds(), updated.getSupplierIds()),
        () -> assertEquals(10, updated.getStock().value()),
        () -> assertEquals(current.getId(), updated.getId()));
  }

  private static ProductModel product(
      final ProductImageUrl imageUrl, final Set<ProductSupplierId> supplierIds) {
    return ProductModel.create(
        new ProductId("product-001"),
        new ProductCode("CODE-01"),
        new ProductName("Product A"),
        new ProductBrand("Brand A"),
        new ProductDescription("Product description"),
        new ProductPrice(new BigDecimal("100.00")),
        new ProductPrice(new BigDecimal("120.00")),
        new ProductStock(10),
        new ProductMinimumStock(5),
        new ProductCategoryId(CATEGORY_ID),
        supplierIds,
        imageUrl);
  }
}
