package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.util.Objects;

/**
 * Descripción detallada del producto para el catálogo.
 *
 * <p>El catálogo del lavadero exige una descripción representativa; por eso el texto es
 * obligatorio y se acota su longitud para conservar la legibilidad del documento.
 */
public record ProductDescription(String value) {

  private static final int MINIMUM_LENGTH = 5;
  private static final int MAXIMUM_LENGTH = 500;

  public ProductDescription {
    final String normalizedValue =
        Objects.requireNonNull(value, "ProductDescription cannot be null").trim();
    validateNotEmpty(normalizedValue);
    validateLength(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("description");
    }
  }

  private static void validateLength(final String normalizedValue) {
    if (normalizedValue.length() < MINIMUM_LENGTH) {
      throw InvalidProductException.becauseLengthIsTooShort("description", MINIMUM_LENGTH);
    }
    if (normalizedValue.length() > MAXIMUM_LENGTH) {
      throw InvalidProductException.becauseLengthIsTooLong("description", MAXIMUM_LENGTH);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
