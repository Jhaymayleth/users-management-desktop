package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.valueobject.ProductId;

/** Puerto de salida: eliminar un producto del catálogo. */
public interface DeleteProductPort {
  void delete(ProductId productId);
}
