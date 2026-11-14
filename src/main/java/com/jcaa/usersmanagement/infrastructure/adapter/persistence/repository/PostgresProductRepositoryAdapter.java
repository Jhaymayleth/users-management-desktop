package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import com.jcaa.usersmanagement.application.port.out.DeleteProductPort;
import com.jcaa.usersmanagement.application.port.out.GetAllProductsPort;
import com.jcaa.usersmanagement.application.port.out.GetLowStockProductsPort;
import com.jcaa.usersmanagement.application.port.out.GetProductByCodePort;
import com.jcaa.usersmanagement.application.port.out.GetProductByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveProductPort;
import com.jcaa.usersmanagement.application.port.out.SearchProductsPort;
import com.jcaa.usersmanagement.application.port.out.UpdateProductPort;
import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.enums.ProductStatus;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductCode;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import com.jcaa.usersmanagement.domain.valueobject.ProductSupplierId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.ProductPersistenceException;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper.ProductPersistenceMapper;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper.ProductPersistenceMapper.ProductRow;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** PostgreSQL JDBC adapter for the Product output ports. */
public final class PostgresProductRepositoryAdapter
    implements SaveProductPort,
        UpdateProductPort,
        DeleteProductPort,
        GetProductByIdPort,
        GetProductByCodePort,
        GetAllProductsPort,
        SearchProductsPort,
        GetLowStockProductsPort {

  private static final String SELECT_COLUMNS =
      "SELECT id, code, name, brand, description, purchase_price, sale_price, stock, "
          + "minimum_stock, category_id, image_url, status FROM products";
  private static final String SQL_INSERT =
      "INSERT INTO products (id, code, name, brand, description, purchase_price, sale_price, "
          + "stock, minimum_stock, category_id, image_url, status) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String SQL_UPDATE =
      "UPDATE products SET name = ?, brand = ?, description = ?, purchase_price = ?, "
          + "sale_price = ?, stock = ?, minimum_stock = ?, category_id = ?, image_url = ?, "
          + "status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
  private static final String SQL_DELETE = "DELETE FROM products WHERE id = ?";
  private static final String SQL_DELETE_SUPPLIERS =
      "DELETE FROM product_suppliers WHERE product_id = ?";
  private static final String SQL_INSERT_SUPPLIER =
      "INSERT INTO product_suppliers (product_id, supplier_id) VALUES (?, ?)";
  private static final String SQL_SELECT_SUPPLIERS =
      "SELECT supplier_id FROM product_suppliers WHERE product_id = ? ORDER BY supplier_id";

  private final Connection connection;

  public PostgresProductRepositoryAdapter(final Connection connection) {
    this.connection = Objects.requireNonNull(connection, "connection cannot be null");
  }

  @Override
  public ProductModel save(final ProductModel product) {
    Objects.requireNonNull(product, "product cannot be null");
    inTransaction(
        "save",
        () -> {
          try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            bindProductForInsert(statement, product);
            statement.executeUpdate();
          }
          insertSuppliers(product);
          return null;
        });
    return product;
  }

  @Override
  public ProductModel update(final ProductModel product) {
    Objects.requireNonNull(product, "product cannot be null");
    final boolean updated =
        inTransaction(
            "update",
            () -> {
              final int affectedRows;
              try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
                bindProductForUpdate(statement, product);
                affectedRows = statement.executeUpdate();
              }
              if (affectedRows == 0) {
                return false;
              }
              deleteSuppliers(product.getId());
              insertSuppliers(product);
              return true;
            });
    if (!updated) {
      throw ProductPersistenceException.becauseProductWasNotFound(product.getId().value());
    }
    return product;
  }

  @Override
  public void delete(final ProductId productId) {
    Objects.requireNonNull(productId, "productId cannot be null");
    try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
      statement.setObject(1, toUuid(productId.value(), "delete"));
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw ProductPersistenceException.becauseOperationFailed("delete", exception);
    }
  }

  @Override
  public Optional<ProductModel> getById(final ProductId productId) {
    Objects.requireNonNull(productId, "productId cannot be null");
    return findOne(
        SELECT_COLUMNS + " WHERE id = ?",
        toUuid(productId.value(), "find by id"),
        "find by id");
  }

  @Override
  public Optional<ProductModel> getByCode(final ProductCode code) {
    Objects.requireNonNull(code, "code cannot be null");
    return findOne(SELECT_COLUMNS + " WHERE code = ?", code.value(), "find by code");
  }

  @Override
  public List<ProductModel> getAll() {
    return findMany(SELECT_COLUMNS + " ORDER BY name ASC", List.of(), "list all");
  }

  @Override
  public List<ProductModel> search(final SearchProductsQuery query) {
    final SearchProductsQuery effectiveQuery =
        query == null ? SearchProductsQuery.all() : query;
    final StringBuilder sql = new StringBuilder(SELECT_COLUMNS).append(" WHERE 1 = 1");
    final List<Object> parameters = new ArrayList<>();

    if (!isBlank(effectiveQuery.nameFragment())) {
      sql.append(" AND LOWER(name) LIKE ?");
      parameters.add(containsPattern(effectiveQuery.nameFragment()));
    }
    if (!isBlank(effectiveQuery.categoryId())) {
      sql.append(" AND category_id = ?");
      parameters.add(toUuid(effectiveQuery.categoryId(), "search by category"));
    }
    if (!isBlank(effectiveQuery.status())) {
      sql.append(" AND status = ?");
      parameters.add(ProductStatus.fromString(effectiveQuery.status().trim()).name());
    }
    if (!isBlank(effectiveQuery.brandFragment())) {
      sql.append(" AND LOWER(COALESCE(brand, '')) LIKE ?");
      parameters.add(containsPattern(effectiveQuery.brandFragment()));
    }
    sql.append(" ORDER BY name ASC");
    return findMany(sql.toString(), parameters, "search");
  }

  @Override
  public List<ProductModel> getLowStock() {
    return findMany(
        SELECT_COLUMNS + " WHERE stock <= minimum_stock ORDER BY stock ASC",
        List.of(),
        "find low-stock products");
  }

  private Optional<ProductModel> findOne(
      final String sql, final Object parameter, final String operation) {
    final ProductRow row;
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      bindParameter(statement, 1, parameter);
      try (ResultSet resultSet = statement.executeQuery()) {
        row = resultSet.next() ? ProductPersistenceMapper.fromResultSet(resultSet) : null;
      }
    } catch (final SQLException exception) {
      throw ProductPersistenceException.becauseOperationFailed(operation, exception);
    }
    return row == null ? Optional.empty() : Optional.of(toModel(row, operation));
  }

  private List<ProductModel> findMany(
      final String sql, final List<Object> parameters, final String operation) {
    final List<ProductRow> rows = new ArrayList<>();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int index = 0; index < parameters.size(); index++) {
        bindParameter(statement, index + 1, parameters.get(index));
      }
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          rows.add(ProductPersistenceMapper.fromResultSet(resultSet));
        }
      }
    } catch (final SQLException exception) {
      throw ProductPersistenceException.becauseOperationFailed(operation, exception);
    }
    final List<ProductModel> products = new ArrayList<>(rows.size());
    for (final ProductRow row : rows) {
      products.add(toModel(row, operation));
    }
    return List.copyOf(products);
  }

  private ProductModel toModel(final ProductRow row, final String operation) {
    return row.toModel(loadSupplierIds(row.id(), operation));
  }

  private Set<ProductSupplierId> loadSupplierIds(
      final String productId, final String operation) {
    final Set<ProductSupplierId> supplierIds = new LinkedHashSet<>();
    try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_SUPPLIERS)) {
      statement.setObject(1, toUuid(productId, operation));
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          supplierIds.add(new ProductSupplierId(resultSet.getString("supplier_id")));
        }
      }
    } catch (final SQLException exception) {
      throw ProductPersistenceException.becauseOperationFailed(operation, exception);
    }
    return Set.copyOf(supplierIds);
  }

  private void insertSuppliers(final ProductModel product) throws SQLException {
    if (product.getSupplierIds().isEmpty()) {
      return;
    }
    try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_SUPPLIER)) {
      for (final ProductSupplierId supplierId : product.getSupplierIds()) {
        statement.setObject(1, toUuid(product.getId().value(), "save suppliers"));
        statement.setObject(2, toUuid(supplierId.value(), "save suppliers"));
        statement.addBatch();
      }
      statement.executeBatch();
    }
  }

  private void deleteSuppliers(final ProductId productId) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE_SUPPLIERS)) {
      statement.setObject(1, toUuid(productId.value(), "update suppliers"));
      statement.executeUpdate();
    }
  }

  private <T> T inTransaction(final String operation, final SqlWork<T> work) {
    final boolean originalAutoCommit;
    try {
      originalAutoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
    } catch (final SQLException exception) {
      throw ProductPersistenceException.becauseOperationFailed(operation, exception);
    }

    T result = null;
    RuntimeException failure = null;
    try {
      result = work.execute();
      connection.commit();
    } catch (final SQLException exception) {
      rollback(exception);
      failure = ProductPersistenceException.becauseOperationFailed(operation, exception);
    } catch (final RuntimeException exception) {
      rollback(exception);
      failure = exception;
    }

    try {
      connection.setAutoCommit(originalAutoCommit);
    } catch (final SQLException restoreFailure) {
      if (failure == null) {
        failure =
            ProductPersistenceException.becauseOperationFailed(
                "restore transaction settings after " + operation, restoreFailure);
      } else {
        failure.addSuppressed(restoreFailure);
      }
    }
    if (failure != null) {
      throw failure;
    }
    return result;
  }

  private void rollback(final Exception failure) {
    try {
      connection.rollback();
    } catch (final SQLException rollbackFailure) {
      failure.addSuppressed(rollbackFailure);
    }
  }

  private static void bindProductForInsert(
      final PreparedStatement statement, final ProductModel product) throws SQLException {
    statement.setObject(1, toUuid(product.getId().value(), "save"));
    statement.setString(2, product.getCode().value());
    statement.setString(3, product.getName().value());
    statement.setString(4, product.getBrand().value());
    statement.setString(5, product.getDescription().value());
    statement.setBigDecimal(6, product.getPurchasePrice().value());
    statement.setBigDecimal(7, product.getSalePrice().value());
    statement.setInt(8, product.getStock().value());
    statement.setInt(9, product.getMinimumStock().value());
    statement.setObject(10, toUuid(product.getCategoryId().value(), "save"));
    statement.setString(11, product.getImageUrl().value());
    statement.setString(12, product.getStatus().name());
  }

  private static void bindProductForUpdate(
      final PreparedStatement statement, final ProductModel product) throws SQLException {
    statement.setString(1, product.getName().value());
    statement.setString(2, product.getBrand().value());
    statement.setString(3, product.getDescription().value());
    statement.setBigDecimal(4, product.getPurchasePrice().value());
    statement.setBigDecimal(5, product.getSalePrice().value());
    statement.setInt(6, product.getStock().value());
    statement.setInt(7, product.getMinimumStock().value());
    statement.setObject(8, toUuid(product.getCategoryId().value(), "update"));
    statement.setString(9, product.getImageUrl().value());
    statement.setString(10, product.getStatus().name());
    statement.setObject(11, toUuid(product.getId().value(), "update"));
  }

  private static void bindParameter(
      final PreparedStatement statement, final int index, final Object parameter)
      throws SQLException {
    if (parameter instanceof UUID uuid) {
      statement.setObject(index, uuid);
    } else {
      statement.setString(index, (String) parameter);
    }
  }

  private static UUID toUuid(final String value, final String operation) {
    try {
      return UUID.fromString(value);
    } catch (final IllegalArgumentException exception) {
      throw ProductPersistenceException.becauseOperationFailed(
          operation, new IllegalArgumentException("Expected a UUID for product data.", exception));
    }
  }

  private static String containsPattern(final String value) {
    return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
  }

  private static boolean isBlank(final String value) {
    return value == null || value.isBlank();
  }

  @FunctionalInterface
  private interface SqlWork<T> {
    T execute() throws SQLException;
  }
}
