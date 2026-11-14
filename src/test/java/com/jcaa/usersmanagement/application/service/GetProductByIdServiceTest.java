package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetProductByIdPort;
import com.jcaa.usersmanagement.application.service.dto.query.GetProductByIdQuery;
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
import java.util.Set;
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

/** Pruebas para GetProductByIdService – validaci�n, flujo feliz y producto no encontrado. */
@DisplayName("GetProductByIdService")
@ExtendWith(MockitoExtension.class)
class GetProductByIdServiceTest {

  @Mock private GetProductByIdPort getProductByIdPort;

  private GetProductByIdService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service = new GetProductByIdService(getProductByIdPort, validatorFactory.getValidator());
    }
  }

  @Test
  @DisplayName("execute() devuelve un producto existente")
  void shouldReturnExistingProduct() {
    final String productId = "p-001";
    final GetProductByIdQuery query = new GetProductByIdQuery(productId);

    final ProductModel expected =
        ProductModel.create(
            new ProductId(productId),
            new ProductCode("CODE-01"),
            new ProductName("Producto A"),
            new ProductBrand("Marca X"),
            new ProductDescription("Description"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(10),
            new ProductMinimumStock(5),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440001"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440101")),
            new ProductImageUrl("http://example.com/img.jpg"));

    when(getProductByIdPort.getById(any())).thenReturn(Optional.of(expected));

    final ProductModel result = service.execute(query);

    assertAll(
        "recuperaci�n exitosa",
        () -> assertNotNull(result),
        () -> assertEquals(productId, result.getId().value()),
        () -> assertEquals("CODE-01", result.getCode().value()),
        () -> assertEquals("Producto A", result.getName().value()));

    verify(getProductByIdPort).getById(new ProductId(productId));
  }

  @Test
  @DisplayName("execute() lanza ProductNotFoundException cuando el producto no existe")
  void shouldThrowWhenProductNotFound() {
    final String nonExistentId = "p-nonexistent";
    final GetProductByIdQuery query = new GetProductByIdQuery(nonExistentId);

    when(getProductByIdPort.getById(any())).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> service.execute(query));

    verify(getProductByIdPort).getById(new ProductId(nonExistentId));
  }

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el query es inv�lido")
  void shouldThrowWhenQueryIsInvalid() {
    final GetProductByIdQuery query = new GetProductByIdQuery(""); // ID vac�o

    assertThrows(ConstraintViolationException.class, () -> service.execute(query));

    verifyNoInteractions(getProductByIdPort);
  }

  @Test
  @DisplayName("execute() normaliza el ID de producto (sin efectos visibles pero consistente)")
  void shouldNormalizeProductId() {
    final String productId = "p-002";
    final GetProductByIdQuery query = new GetProductByIdQuery(productId);

    final ProductModel expected =
        ProductModel.create(
            new ProductId(productId),
            new ProductCode("CODE-02"),
            new ProductName("Producto B"),
            new ProductBrand("Marca Y"),
            new ProductDescription("Description"),
            new ProductPrice(new BigDecimal("150.00")),
            new ProductPrice(new BigDecimal("180.00")),
            new ProductStock(5),
            new ProductMinimumStock(2),
            new ProductCategoryId("550e8400-e29b-41d4-a716-446655440002"),
            Set.of(new ProductSupplierId("550e8400-e29b-41d4-a716-446655440102")),
            new ProductImageUrl("http://example.com/img2.jpg"));

    when(getProductByIdPort.getById(any())).thenReturn(Optional.of(expected));

    final ProductModel result = service.execute(query);

    verify(getProductByIdPort).getById(new ProductId(productId));
  }
}