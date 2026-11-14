package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas del Value Object ProductCode – código legible y único del catálogo. */
@DisplayName("ProductCode")
class ProductCodeTest {

  @Test
  @DisplayName("normaliza a mayúsculas y elimina espacios")
  void shouldNormalizeAndTrim() {
    final ProductCode code = new ProductCode("  AB-123 ");
    assertEquals("AB-123", code.value());
  }

  @Test
  @DisplayName("rechaza valor nulo")
  void shouldRejectNull() {
    assertThrows(NullPointerException.class, () -> new ProductCode(null));
  }

  @Test
  @DisplayName("rechaza cadena vacía después del trim")
  void shouldRejectEmptyString() {
    assertThrows(InvalidProductException.class, () -> new ProductCode("   "));
  }

  @Test
  @DisplayName("requiere al menos 3 caracteres después de la normalización")
  void shouldValidateMinimumLength() {
    assertThrows(InvalidProductException.class, () -> new ProductCode("A1"));
  }

  @Test
  @DisplayName("acepta formato A123, AB-123, AB-123-XYZ, hasta 20 caracteres")
  void shouldValidateFormat() {
    assertNotNull(new ProductCode("ABC"));
    assertNotNull(new ProductCode("AB-123"));
    assertNotNull(new ProductCode("A1234"));
    assertNotNull(new ProductCode("AB-123-XYZ-456"));
  }

  @Test
  @DisplayName("rechaza símbolos no permitidos y guiones al inicio")
  void shouldRejectInvalidFormat() {
    assertThrows(InvalidProductException.class, () -> new ProductCode("-AB-123"));
    assertThrows(InvalidProductException.class, () -> new ProductCode("AB_123"));
    assertThrows(InvalidProductException.class, () -> new ProductCode("AB 123"));
  }

  @Test
  @DisplayName("comparación por igualdad usa el valor normalizado")
  void shouldCompareByValue() {
    final ProductCode first = new ProductCode("AB-123");
    final ProductCode second = new ProductCode("ab-123 ");
    assertEquals(first, second);
  }

  @Test
  @DisplayName("hashCode es consistente con equals")
  void shouldCalculateHashCode() {
    final ProductCode a = new ProductCode("CODE-01");
    final ProductCode b = new ProductCode("CODE-01");
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  @DisplayName("toString devuelve el valor normalizado")
  void shouldPrintValue() {
    assertEquals("CODE-01", new ProductCode("CODE-01").toString());
  }
}