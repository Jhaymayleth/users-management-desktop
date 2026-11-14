package com.jcaa.usersmanagement.domain.exception;

/** Se lanza cuando no existe un producto con el identificador o el código solicitado. */
public final class ProductNotFoundException extends DomainException {

  private static final String MESSAGE_BY_ID = "The product with id '%s' was not found.";
  private static final String MESSAGE_BY_CODE = "The product with code '%s' was not found.";

  private ProductNotFoundException(final String message) {
    super(message);
  }

  public static ProductNotFoundException becauseIdWasNotFound(final String productId) {
    return new ProductNotFoundException(String.format(MESSAGE_BY_ID, productId));
  }

  public static ProductNotFoundException becauseCodeWasNotFound(final String productCode) {
    return new ProductNotFoundException(String.format(MESSAGE_BY_CODE, productCode));
  }
}
