package com.jcaa.usersmanagement.application.service.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Comando para registrar un producto en el catálogo del lavadero.
 *
 * <p>Transporta datos sin reglas de negocio: las validaciones de forma se declaran aquí y las
 * invariantes del catálogo las aplica el agregado {@code ProductModel}. Los importes se reciben
 * como {@link BigDecimal} para no perder precisión monetaria.
 */
public record CreateProductCommand(
    @NotBlank(message = "code must not be blank")
        @Size(min = 3, max = 20, message = "code must have between 3 and 20 characters")
        String code,
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
    @Min(value = 0, message = "stock must not be negative") int stock,
    @Min(value = 0, message = "minimumStock must not be negative") int minimumStock,
    @NotBlank(message = "categoryId must not be blank") String categoryId,
    @NotEmpty(message = "supplierIds must contain at least one supplier") Set<String> supplierIds,
    @Size(max = 500, message = "imageUrl must have at most 500 characters") String imageUrl) {}
