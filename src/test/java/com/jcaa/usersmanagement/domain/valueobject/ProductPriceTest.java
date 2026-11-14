package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas del Value Object que representa un precio monetario del catálogo. */
@DisplayName("ProductPrice")
class ProductPriceTest {

  @Test
  @DisplayName("normaliza el valor a dos decimales con HALF_UP")
  void shouldNormalizeScale() {
    final ProductPrice price = new ProductPrice(new BigDecimal("1500.005"));

    assertEquals(new BigDecimal("1500.01"), price.value());
  }

  @Test
  @DisplayName("rechaza un precio en cero")
  void shouldRejectZero() {
    assertThrows(InvalidProductException.class, () -> new ProductPrice(BigDecimal.ZERO));
  }

  @Test
  @DisplayName("rechaza un precio negativo")
  void shouldRejectNegative() {
    assertThrows(InvalidProductException.class, () -> new ProductPrice(new BigDecimal("-0.01")));
  }

  @Test
  @DisplayName("suma dos precios conservando la escala")
  void shouldAdd() {
    final ProductPrice result =
        new ProductPrice(new BigDecimal("1000.00"))
            .add(new ProductPrice(new BigDecimal("250.50")));

    assertEquals(new BigDecimal("1250.50"), result.value());
  }

  @Test
  @DisplayName("calcula la diferencia entre dos precios")
  void shouldCalculateDifference() {
    final BigDecimal difference =
        new ProductPrice(new BigDecimal("12000.00"))
            .difference(new ProductPrice(new BigDecimal("10000.00")));

    assertEquals(new BigDecimal("2000.00"), difference);
  }

  @Test
  @DisplayName("compara precios por valor monetario")
  void shouldCompare() {
    final ProductPrice cheap = new ProductPrice(new BigDecimal("1000.00"));
    final ProductPrice expensive = new ProductPrice(new BigDecimal("2000.00"));

    assertEquals(true, cheap.isLessThan(expensive));
    assertEquals(true, expensive.isGreaterThanOrEqualTo(cheap));
  }
}
