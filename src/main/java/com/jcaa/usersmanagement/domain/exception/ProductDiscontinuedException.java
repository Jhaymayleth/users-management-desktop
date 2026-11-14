package com.jcaa.usersmanagement.domain.exception;

/** Se lanza cuando se intenta operar un producto descontinuado. */
public final class ProductDiscontinuedException extends DomainException {

  private static final String MESSAGE_DISCONTINUED =
      "The product with code '%s' is discontinued and cannot receive stock.";

  private ProductDiscontinuedException(final String message) {
    super(message);
  }

  public static ProductDiscontinuedException becauseProductIsDiscontinued(final String productCode) {
    return new ProductDiscontinuedException(String.format(MESSAGE_DISCONTINUED, productCode));
  }
}
