package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.DeleteProductUseCase;
import com.jcaa.usersmanagement.application.port.out.DeleteProductPort;
import com.jcaa.usersmanagement.application.port.out.GetProductByIdPort;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteProductCommand;
import com.jcaa.usersmanagement.application.service.mapper.ProductApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.ProductNotFoundException;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/** Caso de uso: retirar un producto del catálogo. */
@Log
@RequiredArgsConstructor
public final class DeleteProductService implements DeleteProductUseCase {

  private final GetProductByIdPort getProductByIdPort;
  private final DeleteProductPort deleteProductPort;
  private final Validator validator;

  @Override
  public void execute(final DeleteProductCommand command) {
    validateCommand(command);

    final ProductId productId = ProductApplicationMapper.fromDeleteCommandToProductId(command);
    ensureProductExists(productId);

    deleteProductPort.delete(productId);
  }

  private void ensureProductExists(final ProductId productId) {
    if (getProductByIdPort.getById(productId).isEmpty()) {
      throw ProductNotFoundException.becauseIdWasNotFound(productId.value());
    }
  }

  private void validateCommand(final DeleteProductCommand command) {
    final Set<ConstraintViolation<DeleteProductCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
