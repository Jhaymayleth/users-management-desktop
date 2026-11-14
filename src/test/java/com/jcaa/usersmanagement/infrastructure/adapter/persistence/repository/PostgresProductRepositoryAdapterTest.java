package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductBrand;
import com.jcaa.usersmanagement.domain.valueobject.ProductCategoryId;
import com.jcaa.usersmanagement.domain.valueobject.ProductCode;
import com.jcaa.usersmanagement.domain.valueobject.ProductDescription;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import com.jcaa.usersmanagement.domain.valueobject.ProductImageUrl;
import com.jcaa.usersmanagement.domain.valueobject.ProductMinimumStock;
import com.jcaa.usersmanagement.domain.valueobject.ProductName;
import com.jcaa.usersmanagement.domain.valueobject.ProductPrice;
import com.jcaa.usersmanagement.domain.valueobject.ProductStock;
import com.jcaa.usersmanagement.domain.valueobject.ProductSupplierId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.ProductPersistenceException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostgresProductRepositoryAdapterTest {

  @Mock private Connection connection;
  @Mock private PreparedStatement productStatement;
  @Mock private PreparedStatement supplierStatement;
  @Mock private ResultSet productResultSet;
  @Mock private ResultSet supplierResultSet;

  private PostgresProductRepositoryAdapter repository;

  @BeforeEach
  void setUp() {
    repository = new PostgresProductRepositoryAdapter(connection);
  }

  @Test
  void savePersistsProductAndSuppliersInOneTransaction() throws SQLException {
    final ProductModel product = productWithSupplier();
    when(connection.getAutoCommit()).thenReturn(true);
    when(connection.prepareStatement(anyString()))
        .thenReturn(productStatement, supplierStatement);

    repository.save(product);

    verify(productStatement).executeUpdate();
    verify(supplierStatement).addBatch();
    verify(supplierStatement).executeBatch();
    verify(connection).commit();
    verify(connection).setAutoCommit(true);
  }

  @Test
  void saveRollsBackWhenTheProductInsertFails() throws SQLException {
    when(connection.getAutoCommit()).thenReturn(true);
    when(connection.prepareStatement(anyString())).thenReturn(productStatement);
    when(productStatement.executeUpdate()).thenThrow(new SQLException("insert failed"));

    assertThrows(ProductPersistenceException.class, () -> repository.save(productWithSupplier()));

    verify(connection).rollback();
    verify(connection).setAutoCommit(true);
  }

  @Test
  void getByIdMapsTheProductAndItsSupplierReferences() throws SQLException {
    final UUID productId = UUID.randomUUID();
    final UUID categoryId = UUID.randomUUID();
    final UUID supplierId = UUID.randomUUID();
    when(connection.prepareStatement(anyString()))
        .thenReturn(productStatement, supplierStatement);
    when(productStatement.executeQuery()).thenReturn(productResultSet);
    when(productResultSet.next()).thenReturn(true, false);
    mockProductRow(productId, categoryId);
    when(supplierStatement.executeQuery()).thenReturn(supplierResultSet);
    when(supplierResultSet.next()).thenReturn(true, false);
    when(supplierResultSet.getString("supplier_id")).thenReturn(supplierId.toString());

    final ProductModel result = repository.getById(new ProductId(productId.toString())).orElseThrow();

    assertEquals(productId.toString(), result.getId().value());
    assertEquals(Set.of(new ProductSupplierId(supplierId.toString())), result.getSupplierIds());
  }

  @Test
  void searchBindsOptionalFiltersAndReturnsAnEmptyListWhenNoRowsMatch() throws SQLException {
    when(connection.prepareStatement(anyString())).thenReturn(productStatement);
    when(productStatement.executeQuery()).thenReturn(productResultSet);
    when(productResultSet.next()).thenReturn(false);
    final UUID categoryId = UUID.randomUUID();

    final var products =
        repository.search(new SearchProductsQuery(" Jab ", categoryId.toString(), "LOW_STOCK", null));

    assertEquals(0, products.size());
    verify(connection)
        .prepareStatement(
            argThat(
                sql ->
                    sql.contains("LOWER(name) LIKE ?")
                        && sql.contains("category_id = ?")
                        && sql.contains("status = ?")));
    verify(productStatement).setString(1, "%jab%");
    verify(productStatement).setObject(2, categoryId);
    verify(productStatement).setString(3, "LOW_STOCK");
  }

  @Test
  void updateReportsWhenTheProductDoesNotExist() throws SQLException {
    when(connection.getAutoCommit()).thenReturn(true);
    when(connection.prepareStatement(anyString())).thenReturn(productStatement);
    when(productStatement.executeUpdate()).thenReturn(0);

    assertThrows(ProductPersistenceException.class, () -> repository.update(productWithSupplier()));

    verify(connection).commit();
    verify(connection).setAutoCommit(true);
  }

  @Test
  void mapperRejectsAStatusThatConflictsWithTheProductStock() throws SQLException {
    mockProductRow(UUID.randomUUID(), UUID.randomUUID());
    when(productResultSet.getString("status")).thenReturn("AVAILABLE");
    when(productResultSet.getInt("stock")).thenReturn(1);
    when(productResultSet.getInt("minimum_stock")).thenReturn(1);

    assertThrows(
        InvalidProductException.class,
        () ->
            com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper
                .ProductPersistenceMapper.fromResultSet(productResultSet)
                .toModel(Set.of()));
  }

  private void mockProductRow(final UUID productId, final UUID categoryId) throws SQLException {
    when(productResultSet.getString("id")).thenReturn(productId.toString());
    when(productResultSet.getString("code")).thenReturn("JAB-001");
    when(productResultSet.getString("name")).thenReturn("Jabon");
    when(productResultSet.getString("brand")).thenReturn("Lavadero");
    when(productResultSet.getString("description")).thenReturn("Producto de prueba");
    when(productResultSet.getBigDecimal("purchase_price")).thenReturn(new BigDecimal("100.00"));
    when(productResultSet.getBigDecimal("sale_price")).thenReturn(new BigDecimal("150.00"));
    when(productResultSet.getInt("stock")).thenReturn(10);
    when(productResultSet.getInt("minimum_stock")).thenReturn(1);
    when(productResultSet.getString("category_id")).thenReturn(categoryId.toString());
    when(productResultSet.getString("image_url")).thenReturn(null);
    when(productResultSet.getString("status")).thenReturn("AVAILABLE");
  }

  private static ProductModel productWithSupplier() {
    return ProductModel.create(
        new ProductId(UUID.randomUUID().toString()),
        new ProductCode("JAB-001"),
        new ProductName("Jabon"),
        new ProductBrand("Lavadero"),
        new ProductDescription("Producto de prueba"),
        new ProductPrice(new BigDecimal("100.00")),
        new ProductPrice(new BigDecimal("150.00")),
        new ProductStock(10),
        new ProductMinimumStock(1),
        new ProductCategoryId(UUID.randomUUID().toString()),
        Set.of(new ProductSupplierId(UUID.randomUUID().toString())),
        new ProductImageUrl(null));
  }
}
