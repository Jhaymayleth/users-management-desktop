package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.ProductPersistenceException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Opens JDBC connections to PostgreSQL without an ORM or connection framework. */
public final class PostgresDatabaseConnectionFactory {

  private PostgresDatabaseConnectionFactory() {}

  public static Connection createConnection(final PostgresDatabaseConfig config) {
    try {
      return DriverManager.getConnection(
          config.buildJdbcUrl(), config.username(), config.password());
    } catch (final SQLException exception) {
      throw ProductPersistenceException.becauseOperationFailed(
          "open PostgreSQL connection", exception);
    }
  }
}
