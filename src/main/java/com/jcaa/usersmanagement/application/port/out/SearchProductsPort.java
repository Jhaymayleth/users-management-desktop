package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.List;

/** Puerto de salida: buscar productos con los criterios indicados por la consulta. */
public interface SearchProductsPort {
  List<ProductModel> search(SearchProductsQuery query);
}
