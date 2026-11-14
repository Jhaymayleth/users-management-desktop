package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.util.Objects;

/**
 * Marca del producto.
 *
 * <p>Es opcional porque el CEA indica que solo <em>algunos</em> productos tienen marca. Cuando se
 * informa, no puede ser un texto vacío y no debe superar la longitud permitida.
 */
public record ProductBrand(String value) {

  private static final int MAXIMUM_LENGTH = 100;

  public ProductBrand {
    if (value != null) {
      final String normalizedValue = value.trim();
      validateNotEmpty(normalizedValue);
      validateLength(normalizedValue);
      value = normalizedValue;
    }
  }

  /** Indica si el producto tiene marca registrada en el catálogo. */
  public boolean isPresent() {
    return Objects.nonNull(value) && !value.isEmpty();
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("brand");
    }
  }

  private static void validateLength(final String normalizedValue) {
    if (normalizedValue.length() > MAXIMUM_LENGTH) {
      throw InvalidProductException.becauseLengthIsTooLong("brand", MAXIMUM_LENGTH);
    }
  }

  @Override
  public String toString() {
    return value == null ? "" : value;
  }
}
