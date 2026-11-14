package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.GetAllProductsUseCase;
import com.jcaa.usersmanagement.application.port.out.GetAllProductsPort;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;
import lombok.RequiredArgsConstructor;

/** Caso de uso: listar el catálogo completo de productos. */
@RequiredArgsConstructor
public final class GetAllProductsService implements GetAllProductsUseCase {

  private final GetAllProductsPort getAllProductsPort;

  @Override
  public List<ProductModel> execute() {
    return getAllProductsPort.getAll();
  }
}
