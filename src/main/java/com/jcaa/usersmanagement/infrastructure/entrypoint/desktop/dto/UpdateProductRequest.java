package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Datos que el adaptador de entrada recibe para actualizar un producto.
 *
 * <p>El stock no se incluye: el inventario cambia con operaciones de negocio explícitas.
 */
public record UpdateProductRequest(
    String id,
    String name,
    String brand,
    String description,
    BigDecimal purchasePrice,
    BigDecimal salePrice,
    int minimumStock,
    String categoryId,
    Set<String> supplierIds,
    String imageUrl) {}
