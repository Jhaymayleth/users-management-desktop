package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.CreateProductUseCase;
import com.jcaa.usersmanagement.application.port.out.GetProductByCodePort;
import com.jcaa.usersmanagement.application.port.out.SaveProductPort;
import com.jcaa.usersmanagement.application.service.dto.command.CreateProductCommand;
import com.jcaa.usersmanagement.application.service.mapper.ProductApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.ProductAlreadyExistsException;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * Caso de uso: registrar un producto en el catálogo.
 *
 * <p>Orquesta la validación del comando, la unicidad del código de negocio y la persistencia a
 * través del puerto de salida. Las invariantes del catálogo las aplica el agregado.
 */
@Log
@RequiredArgsConstructor
public final class CreateProductService implements CreateProductUseCase {

  private final SaveProductPort saveProductPort;
  private final GetProductByCodePort getProductByCodePort;
  private final Validator validator;

  @Override
  public ProductModel execute(final CreateProductCommand command) {
    validateCommand(command);

    final ProductCode code = new ProductCode(command.code());
    ensureCodeIsNotTaken(code);

    final ProductModel productToSave = ProductApplicationMapper.fromCreateCommandToModel(command);
    return saveProductPort.save(productToSave);
  }

  private void validateCommand(final CreateProductCommand command) {
    final Set<ConstraintViolation<CreateProductCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private void ensureCodeIsNotTaken(final ProductCode code) {
    getProductByCodePort
        .getByCode(code)
        .ifPresent(
            ignored -> {
              throw ProductAlreadyExistsException.becauseCodeAlreadyExists(code.value());
            });
  }
}
