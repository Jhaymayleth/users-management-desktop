package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;

/**
 * Puerto de entrada: listar los productos que están por agotarse.
 *
 * <p>Responde a la consulta del CEA «Mostrar los productos que están por agotarse» y usa el stock
 * mínimo de cada producto como umbral de reposición.
 */
public interface GetLowStockProductsUseCase {
  List<ProductModel> execute();
}
