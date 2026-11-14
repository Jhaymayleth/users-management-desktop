package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas del stock del producto: nunca negativo y con operaciones explícitas. */
@DisplayName("ProductStock")
class ProductStockTest {

  @Test
  @DisplayName("rechaza un stock negativo")
  void shouldRejectNegativeStock() {
    assertThrows(InvalidProductException.class, () -> new ProductStock(-1));
  }

  @Test
  @DisplayName("incrementa las unidades recibidas")
  void shouldIncrease() {
    assertEquals(8, new ProductStock(5).increase(3).value());
  }

  @Test
  @DisplayName("decrementa las unidades vendidas")
  void shouldDecrease() {
    assertEquals(6, new ProductStock(10).decrease(4).value());
  }

  @Test
  @DisplayName("no permite dejar el stock en negativo")
  void shouldRejectDecreaseBelowZero() {
    final ProductStock stock = new ProductStock(2);

    assertThrows(InvalidProductException.class, () -> stock.decrease(3));
  }

  @Test
  @DisplayName("identifica el stock vacío")
  void shouldIdentifyEmptyStock() {
    assertTrue(ProductStock.empty().isZero());
  }

  @Test
  @DisplayName("compara contra el stock mínimo del producto")
  void shouldCompareWithMinimumStock() {
    assertTrue(new ProductStock(4).isLessThanOrEqualTo(new ProductMinimumStock(5)));
  }
}
