package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.GetLowStockProductsUseCase;
import com.jcaa.usersmanagement.application.port.out.GetLowStockProductsPort;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: listar los productos que están por agotarse.
 *
 * <p>Responde a la consulta del CEA que alimenta la solicitud de cotizaciones a los proveedores.
 */
@RequiredArgsConstructor
public final class GetLowStockProductsService implements GetLowStockProductsUseCase {

  private final GetLowStockProductsPort getLowStockProductsPort;

  @Override
  public List<ProductModel> execute() {
    return getLowStockProductsPort.getLowStock();
  }
}
