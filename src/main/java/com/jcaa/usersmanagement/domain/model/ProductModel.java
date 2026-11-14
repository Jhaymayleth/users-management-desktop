package com.jcaa.usersmanagement.domain.model;

import com.jcaa.usersmanagement.domain.enums.ProductStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Value;

/**
 * Agregado raíz Product del catálogo del lavadero.
 *
 * <p>El modelo es inmutable: cada operación de negocio devuelve una nueva instancia y conserva la
 * identidad del producto. Concentra las invariantes que cruzan varios Value Objects y mantiene el
 * estado coherente con el stock.
 *
 * <p>La igualdad se define por {@link ProductId}, como corresponde a una entidad de dominio, y no
 * por la totalidad de los atributos.
 */
@Value
public class ProductModel {

  ProductId id;
  ProductCode code;
  ProductName name;
  ProductBrand brand;
  ProductDescription description;
  ProductPrice purchasePrice;
  ProductPrice salePrice;
  ProductStock stock;
  ProductMinimumStock minimumStock;
  ProductCategoryId categoryId;
  Set<ProductSupplierId> supplierIds;
  ProductImageUrl imageUrl;
  ProductStatus status;

  /**
   * Crea un producto nuevo con el estado coherente a su stock inicial.
   *
   * @throws InvalidProductException si el precio de venta es menor que el de compra
   */
  public static ProductModel create(
      final ProductId id,
      final ProductCode code,
      final ProductName name,
      final ProductBrand brand,
      final ProductDescription description,
      final ProductPrice purchasePrice,
      final ProductPrice salePrice,
      final ProductStock stock,
      final ProductMinimumStock minimumStock,
      final ProductCategoryId categoryId,
      final Set<ProductSupplierId> supplierIds,
      final ProductImageUrl imageUrl) {
    validateSalePrice(purchasePrice, salePrice);
    return new ProductModel(
        id,
        code,
        name,
        brand,
        description,
        purchasePrice,
        salePrice,
        stock,
        minimumStock,
        categoryId,
        immutableCopy(supplierIds),
        imageUrl,
        resolveStatus(stock, minimumStock, ProductStatus.AVAILABLE));
  }

  /** Devuelve una copia con los datos editables del catálogo actualizados. */
  public ProductModel updateDetails(
      final ProductName newName,
      final ProductBrand newBrand,
      final ProductDescription newDescription,
      final ProductPrice newPurchasePrice,
      final ProductPrice newSalePrice,
      final ProductMinimumStock newMinimumStock,
      final ProductCategoryId newCategoryId,
      final Set<ProductSupplierId> newSupplierIds,
      final ProductImageUrl newImageUrl) {
    validateSalePrice(newPurchasePrice, newSalePrice);
    return new ProductModel(
        id,
        code,
        newName,
        newBrand,
        newDescription,
        newPurchasePrice,
        newSalePrice,
        stock,
        newMinimumStock,
        newCategoryId,
        immutableCopy(newSupplierIds),
        newImageUrl,
        resolveStatus(stock, newMinimumStock, status));
  }

  /**
   * Devuelve una copia con el stock incrementado tras recibir mercancía.
   *
   * @throws com.jcaa.usersmanagement.domain.exception.ProductDiscontinuedException si el producto
   *     está descontinuado
   */
  public ProductModel receiveStock(final int units) {
    ensureNotDiscontinued();
    final ProductStock updatedStock = stock.increase(units);
    return withStock(updatedStock);
  }

  /**
   * Devuelve una copia con el stock descontado tras una venta.
   *
   * @throws com.jcaa.usersmanagement.domain.exception.InsufficientStockException si no hay
   *     unidades suficientes
   */
  public ProductModel sell(final int units) {
    if (stock.value() < units) {
      throw com.jcaa.usersmanagement.domain.exception.InsufficientStockException
          .becauseAvailableIsLessThanRequested(stock.value(), units);
    }
    return withStock(stock.decrease(units));
  }

  /** Devuelve una copia con el nuevo precio de compra y el de venta recalculado. */
  public ProductModel applyPurchasePrice(
      final ProductPrice newPurchasePrice, final ProductPrice newSalePrice) {
    validateSalePrice(newPurchasePrice, newSalePrice);
    return new ProductModel(
        id,
        code,
        name,
        brand,
        description,
        newPurchasePrice,
        newSalePrice,
        stock,
        minimumStock,
        categoryId,
        supplierIds,
        imageUrl,
        status);
  }

  /** Devuelve una copia marcada como descontinuada. */
  public ProductModel discontinue() {
    return new ProductModel(
        id,
        code,
        name,
        brand,
        description,
        purchasePrice,
        salePrice,
        stock,
        minimumStock,
        categoryId,
        supplierIds,
        imageUrl,
        ProductStatus.DISCONTINUED);
  }

  /** Indica si el producto está en o por debajo del stock mínimo. */
  public boolean isLowStock() {
    return stock.isLessThanOrEqualTo(minimumStock);
  }

  private ProductModel withStock(final ProductStock updatedStock) {
    return new ProductModel(
        id,
        code,
        name,
        brand,
        description,
        purchasePrice,
        salePrice,
        updatedStock,
        minimumStock,
        categoryId,
        supplierIds,
        imageUrl,
        resolveStatus(updatedStock, minimumStock, status));
  }

  private void ensureNotDiscontinued() {
    if (status == ProductStatus.DISCONTINUED) {
      throw com.jcaa.usersmanagement.domain.exception.ProductDiscontinuedException
          .becauseProductIsDiscontinued(code.value());
    }
  }

  private static ProductStatus resolveStatus(
      final ProductStock stock,
      final ProductMinimumStock minimumStock,
      final ProductStatus currentStatus) {
    if (currentStatus == ProductStatus.DISCONTINUED) {
      return ProductStatus.DISCONTINUED;
    }
    if (stock.isZero()) {
      return ProductStatus.OUT_OF_STOCK;
    }
    if (stock.isLessThanOrEqualTo(minimumStock)) {
      return ProductStatus.LOW_STOCK;
    }
    return ProductStatus.AVAILABLE;
  }

  private static void validateSalePrice(
      final ProductPrice purchasePrice, final ProductPrice salePrice) {
    if (salePrice.isLessThan(purchasePrice)) {
      throw InvalidProductException.becauseFormatIsInvalid(
          "sale price", "must be greater than or equal to purchase price");
    }
  }

  private static Set<ProductSupplierId> immutableCopy(final Set<ProductSupplierId> supplierIds) {
    Objects.requireNonNull(supplierIds, "supplierIds cannot be null");
    return Collections.unmodifiableSet(new LinkedHashSet<>(supplierIds));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof ProductModel product)) {
      return false;
    }
    return Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
