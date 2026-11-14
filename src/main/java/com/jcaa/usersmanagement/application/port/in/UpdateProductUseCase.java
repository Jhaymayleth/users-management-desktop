package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** Puerto de entrada: actualizar los datos editables de un producto del catálogo. */
public interface UpdateProductUseCase {
  ProductModel execute(@NotNull @Valid UpdateProductCommand command);
}
