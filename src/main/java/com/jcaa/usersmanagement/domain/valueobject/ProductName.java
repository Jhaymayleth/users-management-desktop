package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.util.Objects;

/**
 * Nombre comercial del producto que se muestra en el catálogo.
 *
 * <p>Se exige una longitud mínima para evitar nombres vacíos o de una sola letra y una máxima para
 * que el catálogo impreso y la interfaz de consola sigan siendo legibles.
 */
public record ProductName(String value) {

  private static final int MINIMUM_LENGTH = 3;
  private static final int MAXIMUM_LENGTH = 150;

  public ProductName {
    final String normalizedValue =
        Objects.requireNonNull(value, "ProductName cannot be null").trim();
    validateNotEmpty(normalizedValue);
    validateLength(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("name");
    }
  }

  private static void validateLength(final String normalizedValue) {
    if (normalizedValue.length() < MINIMUM_LENGTH) {
      throw InvalidProductException.becauseLengthIsTooShort("name", MINIMUM_LENGTH);
    }
    if (normalizedValue.length() > MAXIMUM_LENGTH) {
      throw InvalidProductException.becauseLengthIsTooLong("name", MAXIMUM_LENGTH);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
