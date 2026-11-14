package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;

/** Puerto de salida: recuperar los productos cuyo stock está en o por debajo del mínimo. */
public interface GetLowStockProductsPort {
  List<ProductModel> getLowStock();
}
