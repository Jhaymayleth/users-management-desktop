package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

/** Configuration required to open the local PostgreSQL connection for products. */
public record PostgresDatabaseConfig(
    String host, int port, String databaseName, String username, String password) {

  public String buildJdbcUrl() {
    return String.format("jdbc:postgresql://%s:%d/%s", host, port, databaseName);
  }
}
