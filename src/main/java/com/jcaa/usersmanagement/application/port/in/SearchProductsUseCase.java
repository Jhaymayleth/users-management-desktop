package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Puerto de entrada: buscar productos por nombre, categoría, estado o marca. */
public interface SearchProductsUseCase {
  List<ProductModel> execute(@NotNull SearchProductsQuery query);
}
