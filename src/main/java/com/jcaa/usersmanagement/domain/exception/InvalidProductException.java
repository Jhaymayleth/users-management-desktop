package com.jcaa.usersmanagement.domain.exception;

/**
 * Excepción de dominio para cualquier dato inválido de un producto.
 *
 * <p>Agrupa las validaciones de los Value Objects del agregado {@code Product} con factorías
 * nombradas, de modo que el mensaje describa con precisión la regla de negocio incumplida.
 */
public final class InvalidProductException extends DomainException {

  private static final String MESSAGE_EMPTY = "The product %s must not be empty.";
  private static final String MESSAGE_LENGTH_TOO_SHORT =
      "The product %s must have at least %d characters.";
  private static final String MESSAGE_LENGTH_TOO_LONG =
      "The product %s must have at most %d characters.";
  private static final String MESSAGE_INVALID_FORMAT = "The product %s has an invalid format: '%s'.";
  private static final String MESSAGE_NOT_POSITIVE = "The product %s must be greater than zero.";
  private static final String MESSAGE_NEGATIVE = "The product %s must not be negative.";
  private static final String MESSAGE_INVALID_STATUS = "The product status is invalid: '%s'.";

  private InvalidProductException(final String message) {
    super(message);
  }

  public static InvalidProductException becauseValueIsEmpty(final String field) {
    return new InvalidProductException(String.format(MESSAGE_EMPTY, field));
  }

  public static InvalidProductException becauseLengthIsTooShort(
      final String field, final int minimumLength) {
    return new InvalidProductException(
        String.format(MESSAGE_LENGTH_TOO_SHORT, field, minimumLength));
  }

  public static InvalidProductException becauseLengthIsTooLong(
      final String field, final int maximumLength) {
    return new InvalidProductException(String.format(MESSAGE_LENGTH_TOO_LONG, field, maximumLength));
  }

  public static InvalidProductException becauseFormatIsInvalid(
      final String field, final String value) {
    return new InvalidProductException(String.format(MESSAGE_INVALID_FORMAT, field, value));
  }

  public static InvalidProductException becauseValueIsNotPositive(final String field) {
    return new InvalidProductException(String.format(MESSAGE_NOT_POSITIVE, field));
  }

  public static InvalidProductException becauseValueIsNegative(final String field) {
    return new InvalidProductException(String.format(MESSAGE_NEGATIVE, field));
  }

  public static InvalidProductException becauseStatusIsInvalid(final String value) {
    return new InvalidProductException(String.format(MESSAGE_INVALID_STATUS, value));
  }
}
