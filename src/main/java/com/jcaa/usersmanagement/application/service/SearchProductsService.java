package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.SearchProductsUseCase;
import com.jcaa.usersmanagement.application.port.out.SearchProductsPort;
import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: buscar productos del catálogo.
 *
 * <p>Si la consulta llega nula se interpreta como «todos los productos», para que el adaptador de
 * entrada no tenga que construir una consulta vacía.
 */
@RequiredArgsConstructor
public final class SearchProductsService implements SearchProductsUseCase {

  private final SearchProductsPort searchProductsPort;

  @Override
  public List<ProductModel> execute(final SearchProductsQuery query) {
    final SearchProductsQuery effectiveQuery =
        Objects.isNull(query) ? SearchProductsQuery.all() : query;
    return searchProductsPort.search(effectiveQuery);
  }
}
