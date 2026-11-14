package com.jcaa.usersmanagement.application.service.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Comando para actualizar los datos editables de un producto.
 *
 * <p>No incluye el stock: el inventario se modifica con operaciones explícitas de recepción o de
 * venta, no con una edición genérica. El código tampoco cambia porque identifica el producto en el
 * catálogo.
 */
public record UpdateProductCommand(
    @NotBlank(message = "id must not be blank") String id,
    @NotBlank(message = "name must not be blank")
        @Size(min = 3, max = 150, message = "name must have between 3 and 150 characters")
        String name,
    @Size(max = 100, message = "brand must have at most 100 characters") String brand,
    @NotBlank(message = "description must not be blank")
        @Size(min = 5, max = 500, message = "description must have between 5 and 500 characters")
        String description,
    @NotNull(message = "purchasePrice must not be null")
        @DecimalMin(value = "0.01", message = "purchasePrice must be greater than zero")
        BigDecimal purchasePrice,
    @NotNull(message = "salePrice must not be null")
        @DecimalMin(value = "0.01", message = "salePrice must be greater than zero")
        BigDecimal salePrice,
    @Min(value = 0, message = "minimumStock must not be negative") int minimumStock,
    @NotBlank(message = "categoryId must not be blank") String categoryId,
    Set<String> supplierIds,
    @Size(max = 500, message = "imageUrl must have at most 500 characters") String imageUrl) {}
