package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

import com.jcaa.usersmanagement.domain.enums.ProductStatus;
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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/** Converts PostgreSQL result rows to the Product aggregate model. */
public final class ProductPersistenceMapper {

  private ProductPersistenceMapper() {}

  public static ProductRow fromResultSet(final ResultSet resultSet) throws SQLException {
    return new ProductRow(
        resultSet.getString("id"),
        resultSet.getString("code"),
        resultSet.getString("name"),
        resultSet.getString("brand"),
        resultSet.getString("description"),
        resultSet.getBigDecimal("purchase_price"),
        resultSet.getBigDecimal("sale_price"),
        resultSet.getInt("stock"),
        resultSet.getInt("minimum_stock"),
        resultSet.getString("category_id"),
        resultSet.getString("image_url"),
        resultSet.getString("status"));
  }

  public record ProductRow(
      String id,
      String code,
      String name,
      String brand,
      String description,
      BigDecimal purchasePrice,
      BigDecimal salePrice,
      int stock,
      int minimumStock,
      String categoryId,
      String imageUrl,
      String status) {

    public ProductModel toModel(final Set<ProductSupplierId> supplierIds) {
      final ProductStatus persistedStatus = ProductStatus.fromString(status);
      final ProductModel product =
          ProductModel.create(
              new ProductId(id),
              new ProductCode(code),
              new ProductName(name),
              new ProductBrand(brand),
              new ProductDescription(description),
              new ProductPrice(purchasePrice),
              new ProductPrice(salePrice),
              new ProductStock(stock),
              new ProductMinimumStock(minimumStock),
              new ProductCategoryId(categoryId),
              Set.copyOf(supplierIds),
              new ProductImageUrl(imageUrl));
      if (persistedStatus == ProductStatus.DISCONTINUED) {
        return product.discontinue();
      }
      if (product.getStatus() != persistedStatus) {
        throw InvalidProductException.becauseStatusIsInvalid(status);
      }
      return product;
    }
  }
}
