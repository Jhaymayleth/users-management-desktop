package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.ProductStatus;
import com.jcaa.usersmanagement.domain.exception.InsufficientStockException;
import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import com.jcaa.usersmanagement.domain.exception.ProductDiscontinuedException;
import com.jcaa.usersmanagement.domain.valueobject.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas del agregado ProductModel – flujos de negocio principales. */
@DisplayName("ProductModel")
class ProductModelTest {

  @Test
  @DisplayName("flujo feliz: creación del agregado")
  void shouldCreateProduct() {
    final ProductId id = new ProductId("p-001");
    final ProductCode code = new ProductCode("CODE-01");
    final ProductName name = new ProductName("Producto A");
    final ProductBrand brand = new ProductBrand("Marca X");
    final ProductDescription description = new ProductDescription("Una descripción detallada");
    final ProductPrice purchasePrice = new ProductPrice(BigDecimal.valueOf(100));
    final ProductPrice salePrice = new ProductPrice(BigDecimal.valueOf(120));
    final ProductStock stock = new ProductStock(10);
    final ProductMinimumStock minimumStock = new ProductMinimumStock(5);
    final ProductCategoryId categoryId = new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000");
    final Set<ProductSupplierId> supplierIds = Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440001"));
    final ProductImageUrl imageUrl = new ProductImageUrl("https://example.com/img.jpg");

    final ProductModel product =
        ProductModel.create(
            id,
            code,
            name,
            brand,
            description,
            purchasePrice,
            salePrice,
            stock,
            minimumStock,
            categoryId,
            supplierIds,
            imageUrl);

    assertAll(
        "creación",
        () -> assertEquals(id, product.getId()),
        () -> assertEquals(ProductStatus.AVAILABLE, product.getStatus()),
        () -> assertEquals(10, product.getStock().value()),
        () -> assertFalse(product.isLowStock()));
  }

  @Test
  @DisplayName("rechaza precio de venta menor o igual que compra")
  void shouldRejectSalePriceLowerThanPurchase() {
    final ProductPrice purchase = new ProductPrice(BigDecimal.valueOf(100));
    final ProductPrice sale = new ProductPrice(BigDecimal.valueOf(99));
    assertThrows(
        InvalidProductException.class,
        () -> ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("NombreMuyCorto"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción mínima"),
            purchase,
            sale,
            new ProductStock(0),
            new ProductMinimumStock(0),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null));
  }

  @Test
  @DisplayName("actualizar detalles con nuevos valores válidos")
  void shouldUpdateDetails() {
    final ProductModel original =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("OldName"),
            new ProductBrand("Brand"),
            new ProductDescription("Descripción del producto original"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440001")),
            new ProductImageUrl("http://old.jpg"));

    final ProductModel updated =
        original.updateDetails(
            new ProductName("NewName"),
            new ProductBrand("NewBrand"),
            new ProductDescription("Descripción detallada"),
            new ProductPrice(BigDecimal.valueOf(110)),
            new ProductPrice(BigDecimal.valueOf(130)),
            new ProductMinimumStock(3),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440002"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440003")),
            new ProductImageUrl("http://new.jpg"));

    assertAll(
        "actualización",
        () -> assertEquals("NewName", updated.getName().value()),
        () -> assertEquals("NewBrand", updated.getBrand().toString()),
        () -> assertEquals("550e8400-e29b-41d4-a716-446655440002", updated.getCategoryId().value()),
        () -> assertFalse(updated.isLowStock()) // stock sigue siendo 10 > mínimo 3
        );
  }

  @Test
  @DisplayName("recibir stock – incrementa unidades")
  void shouldReceiveStock() {
    final ProductModel product =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("ProductoBajoStock"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(5),
            new ProductMinimumStock(10),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);

    final ProductModel withStock =
        product.receiveStock(15); // 5 + 15 = 20

    assertAll(
        "recepción",
        () -> assertEquals(20, withStock.getStock().value()),
        () -> assertEquals(ProductStatus.AVAILABLE, withStock.getStatus()));
  }

  @Test
  @DisplayName("vender stock – decrementa unidades, sale del stock si llega a cero")
  void shouldSellStock() {
    final ProductModel product =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("ProductoVenta"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(5),
            new ProductMinimumStock(10),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);

    final ProductModel sold =
        product.sell(2); // 5 - 2 = 3

    assertAll(
        "venta",
        () -> assertEquals(3, sold.getStock().value()),
        () -> assertEquals(ProductStatus.LOW_STOCK, sold.getStatus()));

    // venta hasta cero genera OUT_OF_STOCK
    final ProductModel out =
        product.sell(5); // 5 - 5 = 0
    assertEquals(ProductStatus.OUT_OF_STOCK, out.getStatus());
  }

  @Test
  @DisplayName("rechaza venta cuando el stock es insuficiente")
  void shouldRejectInsufficientStock() {
    final ProductModel product =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("ProductoVendido"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(3),
            new ProductMinimumStock(10),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);

    assertThrows(InsufficientStockException.class, () -> product.sell(5));
  }

  @Test
  @DisplayName("descontinuar marca el estado como DISCONTINUED")
  void shouldDiscontinue() {
    final ProductModel product =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("ProductoDiscontinuado"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(0),
            new ProductMinimumStock(0),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);

    final ProductModel discontinued = product.discontinue();
    assertEquals(ProductStatus.DISCONTINUED, discontinued.getStatus());
  }

  @Test
  @DisplayName("rechaza recepción de stock si está descontinuado")
  void shouldRejectReceiveWhenDiscontinued() {
    final ProductModel product =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("NombreProducto"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(0),
            new ProductMinimumStock(0),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);

    final ProductModel discontinued = product.discontinue();
    assertThrows(
        ProductDiscontinuedException.class, () -> discontinued.receiveStock(10));
  }

  @Test
  @DisplayName("isLowStock detecta stock <= mínimo")
  void shouldIdentifyLowStock() {
    final ProductModel low =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("ProductoBajo"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(3),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);

    assertTrue(low.isLowStock());

    final ProductModel normal =
        ProductModel.create(
            new ProductId("p-2"),
            new ProductCode("CODE-02"),
            new ProductName("ProductoNormal"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);
    assertFalse(normal.isLowStock());
  }

  @Test
  @DisplayName("igualdad por ProductId, no por valores totales")
  void shouldEqualById() {
    final ProductModel p1 =
        ProductModel.create(
            new ProductId("p-1"),
            new ProductCode("CODE-01"),
            new ProductName("ProductoUno"),
            new ProductBrand("Marca"),
            new ProductDescription("Descripción del producto"),
            new ProductPrice(BigDecimal.valueOf(100)),
            new ProductPrice(BigDecimal.valueOf(120)),
            new ProductStock(1),
            new ProductMinimumStock(1),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440000"),
            Collections.emptySet(),
            null);

    final ProductModel p2 =
        ProductModel.create(
            new ProductId("p-1"), // mismo id
            new ProductCode("CODE-99"), // código diferente
            new ProductName("ProductoDos"),
            new ProductBrand("OtraMarca"),
            new ProductDescription("Otra descripción"),
            new ProductPrice(BigDecimal.valueOf(200)),
            new ProductPrice(BigDecimal.valueOf(240)),
            new ProductStock(2),
            new ProductMinimumStock(2),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440002")),
            new ProductImageUrl("http://different.jpg"));

    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
  }
}