package com.jcaa.usersmanagement.domain.enums;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;

/**
 * Estado de disponibilidad de un producto del catálogo del lavadero.
 *
 * <p>El estado se deriva del stock y del stock mínimo del producto: un producto sin unidades está
 * agotado, uno que alcanzó el umbral está por agotarse y uno por encima del umbral está disponible.
 * {@link #DISCONTINUED} es un estado manual para productos que ya no se comercializan.
 */
public enum ProductStatus {
  AVAILABLE,
  LOW_STOCK,
  OUT_OF_STOCK,
  DISCONTINUED;

  /**
   * Convierte un texto almacenado (por ejemplo en base de datos) al estado de dominio.
   *
   * @param value nombre del estado
   * @return el estado correspondiente
   * @throws InvalidProductException si el texto no corresponde a un estado válido
   */
  public static ProductStatus fromString(final String value) {
    for (final ProductStatus status : values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw InvalidProductException.becauseStatusIsInvalid(value);
  }
}
