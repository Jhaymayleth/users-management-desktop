package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.query.GetProductByIdQuery;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** Puerto de entrada: consultar un producto del catálogo por su identificador. */
public interface GetProductByIdUseCase {
  ProductModel execute(@NotNull @Valid GetProductByIdQuery query);
}
