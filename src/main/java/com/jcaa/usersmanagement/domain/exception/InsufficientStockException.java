package com.jcaa.usersmanagement.domain.exception;

/** Se lanza cuando se intenta vender más unidades de las disponibles en stock. */
public final class InsufficientStockException extends DomainException {

  private static final String MESSAGE_INSUFFICIENT =
      "The product has %d units available and %d were requested.";

  private InsufficientStockException(final String message) {
    super(message);
  }

  public static InsufficientStockException becauseAvailableIsLessThanRequested(
      final int availableUnits, final int requestedUnits) {
    return new InsufficientStockException(
        String.format(MESSAGE_INSUFFICIENT, availableUnits, requestedUnits));
  }
}
