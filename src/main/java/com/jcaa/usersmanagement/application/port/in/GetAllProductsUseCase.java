package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;

/** Puerto de entrada: listar el catálogo completo de productos. */
public interface GetAllProductsUseCase {
  List<ProductModel> execute();
}
