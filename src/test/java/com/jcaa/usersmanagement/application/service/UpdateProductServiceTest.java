package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetProductByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateProductPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
import com.jcaa.usersmanagement.domain.enums.ProductStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import com.jcaa.usersmanagement.domain.exception.ProductNotFoundException;
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
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas para UpdateProductService – validaci�n, flujo feliz y precio de venta inv�lido. */
@DisplayName("UpdateProductService")
@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

  @Mock private GetProductByIdPort getProductByIdPort;
  @Mock private UpdateProductPort updateProductPort;

  private UpdateProductService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new UpdateProductService(
              getProductByIdPort, updateProductPort, validatorFactory.getValidator());
    }
  }

  @Test
  @DisplayName("execute() actualiza un producto existente con nuevos valores v�lidos")
  void shouldUpdateExistingProduct() {
    final String productId = "p-001";
    final UpdateProductCommand command =
        new UpdateProductCommand(
            productId,
            "Producto Actualizado",
            "Marca Nueva",
            "Nueva descripci�n",
            new BigDecimal("120.00"),
            new BigDecimal("140.00"),
            8,
            "550e8400-e29b-41d4-a716-446655440003",
            Set.of("550e8400-e29b-41d4-a716-446655440103", "550e8400-e29b-41d4-a716-446655440104"),
            "http://nuevo.jpg");

    final ProductModel existing =
        ProductModel.create(
            new ProductId(productId),
            new ProductCode("CODE-01"),
            new ProductName("Producto Original"),
            new ProductBrand("Marca Original"),
            new ProductDescription("Descripci�n original"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440101")),
            new ProductImageUrl("http://original.jpg"));

    final ProductModel updated =
        existing.updateDetails(
            new ProductName("Producto Actualizado"),
            new ProductBrand("Marca Nueva"),
            new ProductDescription("Nueva descripci�n"),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductPrice(new BigDecimal("140.00")),
            new ProductMinimumStock(8), // nuevo stock m�nimo
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440003"),
            Set.of(
                new ProductSupplierId("550e8400-e29b-41d4-a716-446655440103"),
                new ProductSupplierId("550e8400-e29b-41d4-a716-446655440104")),
            new ProductImageUrl("http://nuevo.jpg"));

    when(getProductByIdPort.getById(any())).thenReturn(Optional.of(existing));
    when(updateProductPort.update(any())).thenReturn(updated);

    final ProductModel result = service.execute(command);

    assertAll(
        "actualizaci�n exitosa",
        () -> assertNotNull(result),
        () -> assertEquals(productId, result.getId().value()),
        () -> assertEquals("Producto Actualizado", result.getName().value()),
        () -> assertEquals("Marca Nueva", result.getBrand().toString()),
        () -> assertEquals(8, result.getMinimumStock().value()),
        () -> assertEquals("550e8400-e29b-41d4-a716-446655440003", result.getCategoryId().value()),
        () ->
            assertTrue(
                result.getSupplierIds().stream()
                    .anyMatch(s -> s.value().equals("550e8400-e29b-41d4-a716-446655440103"))));

    verify(getProductByIdPort).getById(new ProductId(productId));
    verify(updateProductPort).update(updated);
  }

  @Test
  @DisplayName("execute() lanza ProductNotFoundException cuando el producto no existe")
  void shouldThrowWhenProductNotFound() {
    final String nonExistentId = "p-nonexistent";
    final UpdateProductCommand command =
        new UpdateProductCommand(
            nonExistentId,
            "Nuevo Nombre",
            "Nueva Marca",
            "Nueva Descripci�n",
            new BigDecimal("130.00"),
            new BigDecimal("150.00"),
            7,
            "550e8400-e29b-41d4-a716-446655440004",
            Set.of("550e8400-e29b-41d4-a716-446655440105"),
            "http://nuevo.jpg");

    when(getProductByIdPort.getById(any())).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> service.execute(command));

    verify(getProductByIdPort).getById(new ProductId(nonExistentId));
    verify(updateProductPort, never()).update(any());
  }

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el command es inv�lido")
  void shouldThrowWhenCommandIsInvalid() {
    final UpdateProductCommand command =
        new UpdateProductCommand(
            "p-001",
            "", // nombre vac�o
            "", // marca vac�a
            "", // descripci�n vac�a
            new BigDecimal("0.00"), // precio de compra cero
            new BigDecimal("0.00"), // precio de venta cero
            -5, // stock m�nimo negativo
            "", // categoryId vac�o
            null, // supplierIds nulo
            null); // imageUrl nulo

    assertThrows(ConstraintViolationException.class, () -> service.execute(command));

    verifyNoInteractions(getProductByIdPort, updateProductPort);
  }

  @Test
  @DisplayName("execute() lanza InvalidProductException cuando el precio de venta es menor que el de compra")
  void shouldThrowWhenSalePriceInvalid() {
    final String productId = "p-002";
    final UpdateProductCommand command =
        new UpdateProductCommand(
            productId,
            "Producto X",
            "Marca Y",
            "Descripci�n",
            new BigDecimal("100.00"),
            new BigDecimal("90.00"), // precio de venta menor que compra
            5,
            "550e8400-e29b-41d4-a716-446655440005",
            Set.of("550e8400-e29b-41d4-a716-446655440106"),
            "http://ejemplo.jpg");

    final ProductModel existing =
        ProductModel.create(
            new ProductId(productId),
            new ProductCode("CODE-02"),
            new ProductName("Producto Original"),
            new ProductBrand("Marca Original"),
            new ProductDescription("Descripci�n original"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440101")),
            new ProductImageUrl("http://original.jpg"));

    when(getProductByIdPort.getById(any())).thenReturn(Optional.of(existing));

    assertThrows(InvalidProductException.class, () -> service.execute(command));

    verify(getProductByIdPort).getById(new ProductId(productId));
    verify(updateProductPort, never()).update(any());
  }

  @Test
  @DisplayName("execute() conserva campos no editables (stock, status, id, code)")
  void shouldPreserveNonEditableFields() {
    final String productId = "p-003";
    final UpdateProductCommand command =
        new UpdateProductCommand(
            productId,
            "Nuevo Nombre",
            "Nueva Marca",
            "Nueva Descripci�n",
            new BigDecimal("110.00"),
            new BigDecimal("130.00"),
            6,
            "550e8400-e29b-41d4-a716-446655440006",
            Set.of("550e8400-e29b-41d4-a716-446655440107"),
            "http://ejemplo.jpg");

    final ProductModel existing =
        ProductModel.create(
            new ProductId(productId),
            new ProductCode("CODE-03"),
            new ProductName("Nombre Original"),
            new ProductBrand("Marca Original"),
            new ProductDescription("Descripci�n original"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440101")),
            new ProductImageUrl("http://original.jpg"));

    final ProductModel updated = existing.updateDetails(
        new ProductName("Nuevo Nombre"),
        new ProductBrand("Nueva Marca"),
        new ProductDescription("Nueva Descripci�n"),
        new ProductPrice(new BigDecimal("110.00")),
        new ProductPrice(new BigDecimal("130.00")),
        new ProductMinimumStock(6),
        new ProductCategoryId("550e8400-e29b-41d4-a716-446655440006"),
        Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440107")),
        new ProductImageUrl("http://ejemplo.jpg"));

    when(getProductByIdPort.getById(any())).thenReturn(Optional.of(existing));
    when(updateProductPort.update(any())).thenReturn(updated);

    final ProductModel result = service.execute(command);

    assertAll(
        "campos no editables preservados",
        () -> assertEquals(productId, result.getId().value()),
        () -> assertEquals("CODE-03", result.getCode().value()),
        () -> assertEquals(10, result.getStock().value()), // stock original conservado
        () -> assertEquals(com.jcaa.usersmanagement.domain.enums.ProductStatus.AVAILABLE, result.getStatus()));

    verify(updateProductPort).update(updated);
  }
}