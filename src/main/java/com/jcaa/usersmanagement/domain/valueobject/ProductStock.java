package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;

/**
 * Cantidad de unidades disponibles de un producto en bodega.
 *
 * <p>El stock nunca puede ser negativo. Los cambios de inventario se expresan con operaciones
 * explícitas ({@link #increase(int)}, {@link #decrease(int)}) para que la regla quede en el
 * dominio y no en los servicios de aplicación.
 */
public record ProductStock(int value) {

  public ProductStock {
    if (value < 0) {
      throw InvalidProductException.becauseValueIsNegative("stock");
    }
  }

  /** Crea un stock vacío, usado cuando un producto agota sus unidades. */
  public static ProductStock empty() {
    return new ProductStock(0);
  }

  /** Incrementa las unidades disponibles después de recibir mercancía. */
  public ProductStock increase(final int units) {
    validateNonNegative(units, "units to increase");
    return new ProductStock(Math.addExact(value, units));
  }

  /** Descuenta unidades vendidas o aplicadas, sin permitir stock negativo. */
  public ProductStock decrease(final int units) {
    validateNonNegative(units, "units to decrease");
    final int resultingStock = Math.subtractExact(value, units);
    if (resultingStock < 0) {
      throw InvalidProductException.becauseValueIsNegative("stock");
    }
    return new ProductStock(resultingStock);
  }

  /** Indica si el producto no tiene unidades disponibles. */
  public boolean isZero() {
    return value == 0;
  }

  /** Indica si el stock está en o por debajo de un umbral dado. */
  public boolean isLessThanOrEqualTo(final int threshold) {
    return value <= threshold;
  }

  /** Indica si el stock está en o por debajo del stock mínimo configurado. */
  public boolean isLessThanOrEqualTo(final ProductMinimumStock minimumStock) {
    return value <= minimumStock.value();
  }

  private static void validateNonNegative(final int units, final String field) {
    if (units < 0) {
      throw InvalidProductException.becauseValueIsNegative(field);
    }
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
