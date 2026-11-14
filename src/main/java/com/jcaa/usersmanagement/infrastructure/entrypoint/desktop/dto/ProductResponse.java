package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Representación del producto que el adaptador de entrada muestra al usuario.
 *
 * <p>Incluye datos derivados (estado y bandera de stock bajo) para que la consola no tenga que
 * recalcular reglas de negocio.
 */
public record ProductResponse(
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
    Set<String> supplierIds,
    String imageUrl,
    String status,
    boolean lowStock) {}
