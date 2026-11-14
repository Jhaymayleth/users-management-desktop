package com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception;

/** Reports a PostgreSQL persistence failure without exposing driver details to application code. */
public final class ProductPersistenceException extends RuntimeException {

  private ProductPersistenceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public static ProductPersistenceException becauseOperationFailed(
      final String operation, final Throwable cause) {
    return new ProductPersistenceException("Failed to " + operation + " product data.", cause);
  }

  public static ProductPersistenceException becauseProductWasNotFound(final String productId) {
    return new ProductPersistenceException(
        "Product with ID '" + productId + "' was not found.", null);
  }
}
