package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ProductModel;

/** Puerto de salida: persistir los cambios de un producto existente. */
public interface UpdateProductPort {
  ProductModel update(ProductModel product);
}
