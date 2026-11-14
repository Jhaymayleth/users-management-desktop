package com.jcaa.usersmanagement.domain.exception;

/** Se lanza cuando se intenta registrar un producto con un código que ya existe. */
public final class ProductAlreadyExistsException extends DomainException {

  private static final String MESSAGE_CODE_EXISTS = "A product with code '%s' already exists.";

  private ProductAlreadyExistsException(final String message) {
    super(message);
  }

  public static ProductAlreadyExistsException becauseCodeAlreadyExists(final String productCode) {
    return new ProductAlreadyExistsException(String.format(MESSAGE_CODE_EXISTS, productCode));
  }
}
