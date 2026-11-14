package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import java.util.Optional;

/** Puerto de salida: recuperar un producto por su identificador. */
public interface GetProductByIdPort {
  Optional<ProductModel> getById(ProductId productId);
}
