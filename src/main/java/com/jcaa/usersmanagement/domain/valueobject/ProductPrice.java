package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Precio monetario de un producto expresado en pesos colombianos (COP).
 *
 * <p>Se usa la misma abstracción para el precio de compra y el de venta. La escala se fija en dos
 * decimales con redondeo {@link RoundingMode#HALF_UP}, de modo que dos instancias con el mismo
 * valor monetario sean iguales sin importar cómo se hayan construido.
 */
public record ProductPrice(BigDecimal value) implements Comparable<ProductPrice> {

  private static final int SCALE = 2;
  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  public ProductPrice {
    Objects.requireNonNull(value, "ProductPrice cannot be null");
    validatePositive(value);
    value = value.setScale(SCALE, ROUNDING_MODE);
  }

  private static void validatePositive(final BigDecimal candidate) {
    if (candidate.signum() <= 0) {
      throw InvalidProductException.becauseValueIsNotPositive("price");
    }
  }

  /** Suma dos precios conservando la escala monetaria. */
  public ProductPrice add(final ProductPrice other) {
    Objects.requireNonNull(other, "ProductPrice to add cannot be null");
    return new ProductPrice(value.add(other.value));
  }

  /** Resta dos precios; el resultado debe seguir siendo un precio válido (mayor que cero). */
  public ProductPrice subtract(final ProductPrice other) {
    Objects.requireNonNull(other, "ProductPrice to subtract cannot be null");
    return new ProductPrice(value.subtract(other.value));
  }

  /** Diferencia monetaria sin exigir que el resultado sea positivo. */
  public BigDecimal difference(final ProductPrice other) {
    Objects.requireNonNull(other, "ProductPrice to compare cannot be null");
    return value.subtract(other.value);
  }

  /** Multiplica el precio por un factor (porcentajes, cantidades) y devuelve el valor monetario. */
  public ProductPrice multiply(final BigDecimal factor) {
    Objects.requireNonNull(factor, "factor cannot be null");
    return new ProductPrice(value.multiply(factor).setScale(SCALE, ROUNDING_MODE));
  }

  /** Indica si este precio es mayor o igual que otro. */
  public boolean isGreaterThanOrEqualTo(final ProductPrice other) {
    return compareTo(other) >= 0;
  }

  /** Indica si este precio es menor que otro. */
  public boolean isLessThan(final ProductPrice other) {
    return compareTo(other) < 0;
  }

  @Override
  public int compareTo(final ProductPrice other) {
    Objects.requireNonNull(other, "ProductPrice to compare cannot be null");
    return value.compareTo(other.value);
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }
}
