package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Datos que el adaptador de entrada recibe para registrar un producto.
 *
 * <p>Es un DTO de frontera: no contiene validaciones de negocio ni conoce el modelo de dominio. El
 * controlador lo traduce a un comando de aplicación.
 */
public record CreateProductRequest(
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
    String imageUrl) {}
