package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.DeleteProductPort;
import com.jcaa.usersmanagement.application.port.out.GetProductByIdPort;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteProductCommand;
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

/** Pruebas para DeleteProductService – validaci�n, flujo feliz y existencia del producto. */
@DisplayName("DeleteProductService")
@ExtendWith(MockitoExtension.class)
class DeleteProductServiceTest {

  @Mock private GetProductByIdPort getProductByIdPort;
  @Mock private DeleteProductPort deleteProductPort;

  private DeleteProductService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new DeleteProductService(
              getProductByIdPort, deleteProductPort, validatorFactory.getValidator());
    }
  }

  @Test
  @DisplayName("execute() elimina un producto existente")
  void shouldDeleteExistingProduct() {
    final String productId = "p-001";
    final DeleteProductCommand command = new DeleteProductCommand(productId);

    final ProductModel existing =
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

    when(getProductByIdPort.getById(any())).thenReturn(Optional.of(existing));

    service.execute(command);

    verify(getProductByIdPort).getById(new ProductId(productId));
    verify(deleteProductPort).delete(new ProductId(productId));
  }

  @Test
  @DisplayName("execute() lanza ProductNotFoundException cuando el producto no existe")
  void shouldThrowWhenProductNotFound() {
    final String nonExistentId = "p-nonexistent";
    final DeleteProductCommand command = new DeleteProductCommand(nonExistentId);

    when(getProductByIdPort.getById(any())).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> service.execute(command));

    verify(getProductByIdPort).getById(new ProductId(nonExistentId));
    verify(deleteProductPort, never()).delete(any());
  }

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el command es inv�lido")
  void shouldThrowWhenCommandIsInvalid() {
    final DeleteProductCommand command = new DeleteProductCommand(""); // ID vac�o

    assertThrows(ConstraintViolationException.class, () -> service.execute(command));

    verifyNoInteractions(getProductByIdPort, deleteProductPort);
  }

  @Test
  @DisplayName("execute() normaliza el ID de producto (sin efectos visibles pero consistente)")
  void shouldNormalizeProductId() {
    final String productId = "p-002";
    final DeleteProductCommand command = new DeleteProductCommand(productId);

    final ProductModel existing =
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

    when(getProductByIdPort.getById(any())).thenReturn(Optional.of(existing));

    service.execute(command);

    verify(getProductByIdPort).getById(new ProductId(productId));
  }
}