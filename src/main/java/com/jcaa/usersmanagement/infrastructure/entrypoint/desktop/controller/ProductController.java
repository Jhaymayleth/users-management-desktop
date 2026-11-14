package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller;

import com.jcaa.usersmanagement.application.port.in.CreateProductUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteProductUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.GetLowStockProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.GetProductByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.SearchProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateProductUseCase;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateProductRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.ProductResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateProductRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.mapper.ProductDesktopMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Adaptador de entrada del catálogo.
 *
 * <p>Traduce las peticiones de la consola a comandos y consultas de aplicación, y devuelve DTO de
 * respuesta. No contiene reglas de negocio.
 */
@RequiredArgsConstructor
public final class ProductController {

  private final CreateProductUseCase createProductUseCase;
  private final UpdateProductUseCase updateProductUseCase;
  private final DeleteProductUseCase deleteProductUseCase;
  private final GetProductByIdUseCase getProductByIdUseCase;
  private final GetAllProductsUseCase getAllProductsUseCase;
  private final SearchProductsUseCase searchProductsUseCase;
  private final GetLowStockProductsUseCase getLowStockProductsUseCase;

  public List<ProductResponse> listProducts() {
    return ProductDesktopMapper.toResponseList(getAllProductsUseCase.execute());
  }

  public ProductResponse findProductById(final String id) {
    return ProductDesktopMapper.toResponse(
        getProductByIdUseCase.execute(ProductDesktopMapper.toGetByIdQuery(id)));
  }

  public ProductResponse createProduct(final CreateProductRequest request) {
    return ProductDesktopMapper.toResponse(
        createProductUseCase.execute(ProductDesktopMapper.toCreateCommand(request)));
  }

  public ProductResponse updateProduct(final UpdateProductRequest request) {
    return ProductDesktopMapper.toResponse(
        updateProductUseCase.execute(ProductDesktopMapper.toUpdateCommand(request)));
  }

  public void deleteProduct(final String id) {
    deleteProductUseCase.execute(ProductDesktopMapper.toDeleteCommand(id));
  }

  public List<ProductResponse> searchProducts(
      final String nameFragment,
      final String categoryId,
      final String status,
      final String brandFragment) {
    return ProductDesktopMapper.toResponseList(
        searchProductsUseCase.execute(
            ProductDesktopMapper.toSearchQuery(nameFragment, categoryId, status, brandFragment)));
  }

  public List<ProductResponse> listLowStockProducts() {
    return ProductDesktopMapper.toResponseList(getLowStockProductsUseCase.execute());
  }
}
