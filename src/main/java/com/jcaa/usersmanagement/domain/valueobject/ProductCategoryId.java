package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.util.Objects;
import java.util.UUID;

/**
 * Referencia a la categoría a la que pertenece un producto.
 *
 * <p>El CEA exige que cada producto pertenezca a una única categoría. El agregado Product solo
 * guarda la referencia, no la categoría completa, para mantener los límites del agregado.
 */
public record ProductCategoryId(String value) {

  public ProductCategoryId {
    final String normalizedValue =
        Objects.requireNonNull(value, "ProductCategoryId cannot be null").trim();
    validateNotEmpty(normalizedValue);
    validateUuid(normalizedValue);
    value = normalizedValue;
  }

  /** Crea una referencia de categoría a partir de un UUID. */
  public static ProductCategoryId from(final UUID categoryId) {
    return new ProductCategoryId(Objects.requireNonNull(categoryId, "categoryId cannot be null").toString());
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("category id");
    }
  }

  private static void validateUuid(final String normalizedValue) {
    try {
      UUID.fromString(normalizedValue);
    } catch (IllegalArgumentException exception) {
      throw InvalidProductException.becauseFormatIsInvalid("category id", normalizedValue);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
