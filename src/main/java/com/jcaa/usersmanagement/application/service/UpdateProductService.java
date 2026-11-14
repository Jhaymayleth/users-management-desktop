package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.UpdateProductUseCase;
import com.jcaa.usersmanagement.application.port.out.GetProductByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateProductPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
import com.jcaa.usersmanagement.application.service.mapper.ProductApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.ProductNotFoundException;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: actualizar los datos editables de un producto.
 *
 * <p>El stock no se modifica por esta vía: el inventario se cambia con operaciones explícitas de
 * recepción o de venta, que tienen reglas propias.
 */
@RequiredArgsConstructor
public final class UpdateProductService implements UpdateProductUseCase {

  private final GetProductByIdPort getProductByIdPort;
  private final UpdateProductPort updateProductPort;
  private final Validator validator;

  @Override
  public ProductModel execute(final UpdateProductCommand command) {
    validateCommand(command);

    final ProductId productId = new ProductId(command.id());
    final ProductModel current = findProduct(productId);
    final ProductModel updated = ProductApplicationMapper.fromUpdateCommandToModel(command, current);

    return updateProductPort.update(updated);
  }

  private ProductModel findProduct(final ProductId productId) {
    return getProductByIdPort
        .getById(productId)
        .orElseThrow(() -> ProductNotFoundException.becauseIdWasNotFound(productId.value()));
  }

  private void validateCommand(final UpdateProductCommand command) {
    final Set<ConstraintViolation<UpdateProductCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
