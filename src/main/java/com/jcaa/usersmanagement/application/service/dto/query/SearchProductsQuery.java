package com.jcaa.usersmanagement.application.service.dto.query;

/**
 * Consulta para buscar productos del catálogo con criterios opcionales.
 *
 * <p>Todos los filtros son opcionales: cuando se omiten, la búsqueda devuelve el catálogo
 * completo. El texto se compara sin distinguir mayúsculas ni tildes en el adaptador.
 *
 * @param nameFragment fragmento del nombre del producto
 * @param categoryId identificador de la categoría
 * @param status estado de disponibilidad
 * @param brandFragment fragmento de la marca
 */
public record SearchProductsQuery(
    String nameFragment, String categoryId, String status, String brandFragment) {

  /** Consulta vacía que representa «todos los productos del catálogo». */
  public static SearchProductsQuery all() {
    return new SearchProductsQuery(null, null, null, null);
  }
}
