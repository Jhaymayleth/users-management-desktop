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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.java.Log;

/**
 * Adaptador de salida en memoria para el catálogo de productos.
 *
 * <p>Implementa todos los puertos OUT del producto con un mapa concurrente, de modo que la Unidad 2
 * pueda demostrar el CRUDL sin base de datos. El dominio no cambia cuando la Unidad 3 sustituya
 * este adaptador por uno JDBC sobre PostgreSQL.
 *
 * <p>Las búsquedas normalizan el texto a minúsculas para que el usuario no tenga que recordar
 * mayúsculas ni tildes exactas.
 */
@Log
public final class InMemoryProductRepositoryAdapter
    implements SaveProductPort,
        UpdateProductPort,
        DeleteProductPort,
        GetProductByIdPort,
        GetProductByCodePort,
        GetAllProductsPort,
        SearchProductsPort,
        GetLowStockProductsPort {

  private final Map<String, ProductModel> productsById = new ConcurrentHashMap<>();

  @Override
  public ProductModel save(final ProductModel product) {
    Objects.requireNonNull(product, "product cannot be null");
    productsById.put(product.getId().value(), product);
    log.info(() -> "Producto almacenado en memoria: " + product.getCode().value());
    return product;
  }

  @Override
  public ProductModel update(final ProductModel product) {
    Objects.requireNonNull(product, "product cannot be null");
    productsById.put(product.getId().value(), product);
    return product;
  }

  @Override
  public void delete(final ProductId productId) {
    Objects.requireNonNull(productId, "productId cannot be null");
    productsById.remove(productId.value());
  }

  @Override
  public Optional<ProductModel> getById(final ProductId productId) {
    Objects.requireNonNull(productId, "productId cannot be null");
    return Optional.ofNullable(productsById.get(productId.value()));
  }

  @Override
  public Optional<ProductModel> getByCode(final ProductCode code) {
    Objects.requireNonNull(code, "code cannot be null");
    return productsById.values().stream()
        .filter(product -> product.getCode().equals(code))
        .findFirst();
  }

  @Override
  public List<ProductModel> getAll() {
    return productsById.values().stream()
        .sorted(Comparator.comparing(product -> product.getName().value()))
        .collect(Collectors.toList());
  }

  @Override
  public List<ProductModel> search(final SearchProductsQuery query) {
    final SearchProductsQuery effectiveQuery =
        Objects.isNull(query) ? SearchProductsQuery.all() : query;
    return productsById.values().stream()
        .filter(product -> matchesName(product, effectiveQuery.nameFragment()))
        .filter(product -> matchesBrand(product, effectiveQuery.brandFragment()))
        .filter(product -> matchesCategory(product, effectiveQuery.categoryId()))
        .filter(product -> matchesStatus(product, effectiveQuery.status()))
        .sorted(Comparator.comparing(product -> product.getName().value()))
        .collect(Collectors.toList());
  }

  @Override
  public List<ProductModel> getLowStock() {
    return productsById.values().stream()
        .filter(ProductModel::isLowStock)
        .sorted(Comparator.comparing(product -> product.getStock().value()))
        .collect(Collectors.toList());
  }

  private boolean matchesName(final ProductModel product, final String nameFragment) {
    if (isBlank(nameFragment)) {
      return true;
    }
    return normalize(product.getName().value()).contains(normalize(nameFragment));
  }

  private boolean matchesBrand(final ProductModel product, final String brandFragment) {
    if (isBlank(brandFragment)) {
      return true;
    }
    return normalize(product.getBrand().toString()).contains(normalize(brandFragment));
  }

  private boolean matchesCategory(final ProductModel product, final String categoryId) {
    if (isBlank(categoryId)) {
      return true;
    }
    return product.getCategoryId().value().equalsIgnoreCase(categoryId.trim());
  }

  private boolean matchesStatus(final ProductModel product, final String status) {
    if (isBlank(status)) {
      return true;
    }
    return product.getStatus() == ProductStatus.fromString(status.trim());
  }

  private static boolean isBlank(final String value) {
    return Objects.isNull(value) || value.isBlank();
  }

  private static String normalize(final String value) {
    return Objects.isNull(value) ? "" : value.trim().toLowerCase(Locale.ROOT);
  }
}
