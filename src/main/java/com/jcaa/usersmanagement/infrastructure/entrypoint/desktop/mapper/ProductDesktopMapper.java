package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetProductByIdQuery;
import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateProductRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.ProductResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateProductRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Traductor entre los DTO del adaptador de entrada y los comandos de la capa de aplicación.
 *
 * <p>Aísla la consola del modelo: si mañana se agrega una API REST, reutiliza los mismos comandos
 * sin tocar el dominio.
 */
public final class ProductDesktopMapper {

  private ProductDesktopMapper() {}

  public static CreateProductCommand toCreateCommand(final CreateProductRequest request) {
    return new CreateProductCommand(
        request.code(),
        request.name(),
        request.brand(),
        request.description(),
        request.purchasePrice(),
        request.salePrice(),
        request.stock(),
        request.minimumStock(),
        request.categoryId(),
        request.supplierIds(),
        request.imageUrl());
  }

  public static UpdateProductCommand toUpdateCommand(final UpdateProductRequest request) {
    return new UpdateProductCommand(
        request.id(),
        request.name(),
        request.brand(),
        request.description(),
        request.purchasePrice(),
        request.salePrice(),
        request.minimumStock(),
        request.categoryId(),
        request.supplierIds(),
        request.imageUrl());
  }

  public static SearchProductsQuery toSearchQuery(
      final String nameFragment,
      final String categoryId,
      final String status,
      final String brandFragment) {
    return new SearchProductsQuery(nameFragment, categoryId, status, brandFragment);
  }

  public static GetProductByIdQuery toGetByIdQuery(final String id) {
    return new GetProductByIdQuery(id);
  }

  public static DeleteProductCommand toDeleteCommand(final String id) {
    return new DeleteProductCommand(id);
  }

  public static ProductResponse toResponse(final ProductModel product) {
    final Set<String> suppliers =
        product.getSupplierIds().stream()
            .map(supplierId -> supplierId.value())
            .collect(Collectors.toSet());

    return new ProductResponse(
        product.getId().value(),
        product.getCode().value(),
        product.getName().value(),
        product.getBrand().toString(),
        product.getDescription().value(),
        product.getPurchasePrice().value(),
        product.getSalePrice().value(),
        product.getStock().value(),
        product.getMinimumStock().value(),
        product.getCategoryId().value(),
        suppliers,
        product.getImageUrl().toString(),
        product.getStatus().name(),
        product.isLowStock());
  }

  public static List<ProductResponse> toResponseList(final List<ProductModel> products) {
    return products.stream().map(ProductDesktopMapper::toResponse).toList();
  }
}
