package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetProductByIdQuery;
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
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;

/**
 * Traductor entre los DTO de la capa de aplicación y el modelo de dominio.
 *
 * <p>Es la capa de anticorrupción del caso de uso: convierte datos primitivos a Value Objects y
 * aplica las decisiones de conservación de datos opcionales, sin exponer el modelo al adaptador de
 * entrada.
 */
@UtilityClass
public class ProductApplicationMapper {

  /** Construye un producto nuevo a partir del comando de registro. */
  public ProductModel fromCreateCommandToModel(final CreateProductCommand command) {
    return ProductModel.create(
        new ProductId(UUID.randomUUID().toString()),
        new ProductCode(command.code()),
        new ProductName(command.name()),
        new ProductBrand(command.brand()),
        new ProductDescription(command.description()),
        new ProductPrice(command.purchasePrice()),
        new ProductPrice(command.salePrice()),
        new ProductStock(command.stock()),
        new ProductMinimumStock(command.minimumStock()),
        new ProductCategoryId(command.categoryId()),
        toSupplierIds(command.supplierIds()),
        new ProductImageUrl(command.imageUrl()));
  }

  /**
   * Aplica el comando de actualización sobre el producto cargado.
   *
   * <p>Los campos opcionales que llegan nulos conservan el valor actual del producto, de modo que
   * una actualización parcial no borre información del catálogo.
   */
  public ProductModel fromUpdateCommandToModel(
      final UpdateProductCommand command, final ProductModel current) {
    final ProductImageUrl imageUrl =
        Objects.isNull(command.imageUrl())
            ? current.getImageUrl()
            : new ProductImageUrl(command.imageUrl());
    final Set<ProductSupplierId> supplierIds =
        Objects.isNull(command.supplierIds()) || command.supplierIds().isEmpty()
            ? current.getSupplierIds()
            : toSupplierIds(command.supplierIds());

    return current.updateDetails(
        new ProductName(command.name()),
        new ProductBrand(command.brand()),
        new ProductDescription(command.description()),
        new ProductPrice(command.purchasePrice()),
        new ProductPrice(command.salePrice()),
        new ProductMinimumStock(command.minimumStock()),
        new ProductCategoryId(command.categoryId()),
        supplierIds,
        imageUrl);
  }

  /** Extrae el identificador de la consulta por identificador. */
  public ProductId fromGetProductByIdQueryToProductId(final GetProductByIdQuery query) {
    return new ProductId(query.id());
  }

  /** Extrae el identificador del comando de eliminación. */
  public ProductId fromDeleteCommandToProductId(final DeleteProductCommand command) {
    return new ProductId(command.id());
  }

  private Set<ProductSupplierId> toSupplierIds(final Set<String> supplierIds) {
    Objects.requireNonNull(supplierIds, "supplierIds cannot be null");
    final Set<ProductSupplierId> result = new LinkedHashSet<>();
    supplierIds.forEach(identifier -> result.add(new ProductSupplierId(identifier)));
    return result;
  }
}
