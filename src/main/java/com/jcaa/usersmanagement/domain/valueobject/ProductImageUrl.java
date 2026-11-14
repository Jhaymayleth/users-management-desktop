package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * URL de la imagen representativa del producto en el catálogo.
 *
 * <p>Es opcional: no todos los productos tienen fotografía. Cuando se informa debe ser una URL
 * absoluta con esquema HTTP o HTTPS.
 */
public record ProductImageUrl(String value) {

  public ProductImageUrl {
    if (value != null) {
      final String normalizedValue = value.trim();
      validateNotEmpty(normalizedValue);
      validateAbsoluteHttpUrl(normalizedValue);
      value = normalizedValue;
    }
  }

  /** Indica si el producto tiene imagen asociada. */
  public boolean isPresent() {
    return Objects.nonNull(value) && !value.isEmpty();
  }

  private static void validateNotEmpty(final String normalizedValue) {
    if (normalizedValue.isEmpty()) {
      throw InvalidProductException.becauseValueIsEmpty("image url");
    }
  }

  private static void validateAbsoluteHttpUrl(final String normalizedValue) {
    try {
      final URI uri = new URI(normalizedValue);
      final String scheme = uri.getScheme();
      final boolean isHttp = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
      if (!isHttp || uri.getHost() == null) {
        throw InvalidProductException.becauseFormatIsInvalid("image url", normalizedValue);
      }
    } catch (URISyntaxException exception) {
      throw InvalidProductException.becauseFormatIsInvalid("image url", normalizedValue);
    }
  }

  @Override
  public String toString() {
    return value == null ? "" : value;
  }
}
