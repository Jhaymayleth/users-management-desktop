package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;

/**
 * Umbral a partir del cual un producto se considera "por agotarse".
 *
 * <p>Es un concepto distinto del stock: el stock indica cuántas unidades hay y el stock mínimo
 * indica cuándo el empleado debe solicitar una cotización de compra a los proveedores. Por eso se
 * modela como Value Object propio y no como un entero suelto.
 */
public record ProductMinimumStock(int value) {

  public ProductMinimumStock {
    if (value < 0) {
      throw InvalidProductException.becauseValueIsNegative("minimum stock");
    }
  }

  /** Umbral por defecto cuando el negocio no configura uno específico. */
  public static ProductMinimumStock defaultThreshold() {
    return new ProductMinimumStock(5);
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
