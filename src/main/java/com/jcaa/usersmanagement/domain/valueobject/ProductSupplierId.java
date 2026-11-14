package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.util.Objects;
import java.util.UUID;

/**
 * Referencia a un proveedor que suministra el producto.
 *
 * <p>Un producto puede ser suministrado por uno o varios proveedores. Igual que con la categoría,
 * el agregado conserva solo el identificador para no arrastrar el agregado Supplier completo.
 */
public record ProductSupplierId(String value) {

  public ProductSupplierId {
    final String normalizedValue =
        Objects.requireNonNull(value, "ProductSupplierId cannot be null").trim();
    validateNotEmpty(normalizedValue);
    validateUuid(normalizedValue);
    value = normalizedValue;
  }

  /** Crea una referencia de proveedor a partir de un UUID. */
  public static ProductSupplierId from(final UUID supplierId) {
    return new ProductSupplierId(Objects.requireNonNull(supplierId, "supplierId cannot be null").toString());
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("supplier id");
    }
  }

  private static void validateUuid(final String normalizedValue) {
    try {
      UUID.fromString(normalizedValue);
    } catch (IllegalArgumentException exception) {
      throw InvalidProductException.becauseFormatIsInvalid("supplier id", normalizedValue);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
