package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductCode;
import java.util.Optional;

/** Puerto de salida: recuperar un producto por su código de negocio. */
public interface GetProductByCodePort {
  Optional<ProductModel> getByCode(ProductCode code);
}
