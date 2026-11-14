package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;

/** Puerto de salida: recuperar el catálogo completo de productos. */
public interface GetAllProductsPort {
  List<ProductModel> getAll();
}
