package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.command.DeleteProductCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** Puerto de entrada: retirar un producto del catálogo del lavadero. */
public interface DeleteProductUseCase {
  void execute(@NotNull @Valid DeleteProductCommand command);
}
