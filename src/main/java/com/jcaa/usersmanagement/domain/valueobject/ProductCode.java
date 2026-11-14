package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Código legible y único del producto dentro del catálogo del lavadero.
 *
 * <p>Se normaliza a mayúsculas y solo admite letras, dígitos y guiones. El catálogo muestra este
 * código al cliente, por eso es un identificador de negocio y no un simple campo de texto.
 */
public record ProductCode(String value) {

  private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9\\-]{2,19}$");

  public ProductCode {
    final String normalizedValue =
        Objects.requireNonNull(value, "ProductCode cannot be null").trim().toUpperCase();
    validateNotEmpty(normalizedValue);
    validateFormat(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("code");
    }
  }

  private static void validateFormat(final String normalizedValue) {
    if (!CODE_PATTERN.matcher(normalizedValue).matches()) {
      throw InvalidProductException.becauseFormatIsInvalid("code", normalizedValue);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
