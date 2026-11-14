package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetProductByCodePort;
import com.jcaa.usersmanagement.application.port.out.SaveProductPort;
import com.jcaa.usersmanagement.application.service.dto.command.CreateProductCommand;
import com.jcaa.usersmanagement.domain.exception.ProductAlreadyExistsException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas para CreateProductService – validaci�n, unicidad de c�digo y flujo feliz. */
@DisplayName("CreateProductService")
@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {

  @Mock private SaveProductPort saveProductPort;
  @Mock private GetProductByCodePort getProductByCodePort;

  private CreateProductService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new CreateProductService(
              saveProductPort,
              getProductByCodePort,
              validatorFactory.getValidator());
    }
  }

  @Test
  @DisplayName("execute() guarda el producto cuando el c�digo no est� registrado")
  void shouldSaveProductWhenCodeIsNew() {
    final CreateProductCommand command =
        new CreateProductCommand(
            "CODE-01",
            "Producto A",
            "Marca X",
            "Description",
            new BigDecimal("100.00"),
            new BigDecimal("120.00"),
            10,
            5,
            "550e8400-e29b-41d4-a716-446655440001",
            java.util.Set.of("550e8400-e29b-41d4-a716-446655440101"),
            "http://example.com/img.jpg");

    final ProductModel savedProduct =
        ProductModel.create(
            new ProductId("p-001"),
            new ProductCode("CODE-01"),
            new ProductName("Producto A"),
            new ProductBrand("Marca X"),
            new ProductDescription("Descripción del producto A"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            java.util.Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440002")),
            new ProductImageUrl("http://example.com/img.jpg"));

    when(getProductByCodePort.getByCode(any())).thenReturn(Optional.empty());
    when(saveProductPort.save(any())).thenReturn(savedProduct);

    final ProductModel result = service.execute(command);

    assertAll(
        "creaci�n exitosa",
        () -> assertNotNull(result),
        () -> assertEquals("p-001", result.getId().value()),
        () -> assertEquals("CODE-01", result.getCode().value()));

    verify(saveProductPort).save(any(ProductModel.class));
    verify(getProductByCodePort).getByCode(new ProductCode("CODE-01"));
  }

  @Test
  @DisplayName("execute() lanza ProductAlreadyExistsException cuando el c�digo ya existe")
  void shouldThrowWhenCodeAlreadyExists() {
    final CreateProductCommand command =
        new CreateProductCommand(
            "CODE-02",
            "Producto B",
            "Marca Y",
            "Otra descripci�n",
            new BigDecimal("150.00"),
            new BigDecimal("180.00"),
            5,
            2,
            "550e8400-e29b-41d4-a716-446655440002",
            java.util.Set.of("550e8400-e29b-41d4-a716-446655440102"),
            "http://example.com/img2.jpg");

    final ProductModel existing =
        ProductModel.create(
            new ProductId("p-002"),
            new ProductCode("CODE-02"),
            new ProductName("Producto B"),
            new ProductBrand("Marca Y"),
            new ProductDescription("Otra descripci�n del producto"),
            new ProductPrice(new BigDecimal("150.00")),
            new ProductPrice(new BigDecimal("180.00")),
            new ProductStock(5),
            new ProductMinimumStock(2),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440003"),
            java.util.Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440004")),
            new ProductImageUrl("http://example.com/img2.jpg"));

    when(getProductByCodePort.getByCode(any())).thenReturn(Optional.of(existing));

    assertThrows(ProductAlreadyExistsException.class, () -> service.execute(command));

    verify(saveProductPort, never()).save(any());
  }

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el command es inv�lido")
  void shouldThrowWhenCommandIsInvalid() {
    final CreateProductCommand command =
        new CreateProductCommand(
            "", // c�digo vac�o
            "", // nombre vac�o
            "", // marca vac�a
            "", // descripci�n vac�a
            new BigDecimal("0.00"), // precio de compra cero
            new BigDecimal("0.00"), // precio de venta cero
            -1, // stock negativo
            -5, // m�nimo negativo
            "", // categoryId vac�a
            null, // supplierIds nulo
            null); // imageUrl nulo

    assertThrows(ConstraintViolationException.class, () -> service.execute(command));

    verifyNoInteractions(saveProductPort, getProductByCodePort);
  }

  @Test
  @DisplayName("execute() normaliza el c�digo a may�sculas")
  void shouldNormalizeCode() {
    final CreateProductCommand command =
        new CreateProductCommand(
            "ab-123 ", // entra como ab-123
            "Producto C",
            "Marca Z",
            "Description",
            new BigDecimal("200.00"),
            new BigDecimal("240.00"),
            7,
            3,
            "550e8400-e29b-41d4-a716-446655440003",
            java.util.Set.of("550e8400-e29b-41d4-a716-446655440103"),
            null);

    final ProductCode normalizedCode = new ProductCode("AB-123");
    when(getProductByCodePort.getByCode(any())).thenReturn(Optional.empty());

    final ProductModel result = service.execute(command);

    verify(getProductByCodePort).getByCode(normalizedCode);
  }
}