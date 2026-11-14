package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ProductModel;

/** Puerto de salida: persistir un producto nuevo en el catálogo. */
public interface SaveProductPort {
  ProductModel save(ProductModel product);
}
