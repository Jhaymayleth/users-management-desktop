package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.util.Objects;

/**
 * Identificador único del agregado Product.
 *
 * <p>Se modela como Value Object para que el resto del dominio no dependa del tipo técnico usado
 * para generarlo (UUID, secuencia, etc.).
 */
public record ProductId(String value) {

  public ProductId {
    final String normalizedValue =
        Objects.requireNonNull(value, "ProductId cannot be null").trim();
    validateNotEmpty(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("id");
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
