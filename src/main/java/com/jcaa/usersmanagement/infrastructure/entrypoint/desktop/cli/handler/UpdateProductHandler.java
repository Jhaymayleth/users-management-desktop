package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import com.jcaa.usersmanagement.domain.exception.ProductNotFoundException;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ProductResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.ProductController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateProductRequest;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/** Handler del menú: actualizar los datos editables de un producto. */
@RequiredArgsConstructor
public final class UpdateProductHandler implements OperationHandler {

  private final ProductController productController;
  private final ConsoleIO console;
  private final ProductResponsePrinter printer;

  @Override
  public void handle() {
    final String id = console.readRequired("Product ID      : ");
    final String name = console.readRequired("Name            : ");
    final String brand = console.readOptional("Brand (optional): ");
    final String description = console.readRequired("Description     : ");
    final BigDecimal purchasePrice = readDecimal("Purchase price  : ");
    final BigDecimal salePrice = readDecimal("Sale price      : ");
    final int minimumStock = console.readInt("Minimum stock   : ");
    final String categoryId = console.readRequired("Category ID     : ");
    final Set<String> supplierIds = readSuppliers();
    final String imageUrl = console.readOptional("Image URL       : ");

    final UpdateProductRequest request =
        new UpdateProductRequest(
            id,
            name,
            brand,
            description,
            purchasePrice,
            salePrice,
            minimumStock,
            categoryId,
            supplierIds,
            imageUrl);

    try {
      final var updated = productController.updateProduct(request);
      console.println("\n  Product updated successfully.");
      printer.print(updated);
    } catch (final ProductNotFoundException | InvalidProductException exception) {
      console.println("  Error: " + exception.getMessage());
    }
  }

  private BigDecimal readDecimal(final String prompt) {
    while (true) {
      final String raw = console.readRequired(prompt);
      try {
        return new BigDecimal(raw.replace(",", "."));
      } catch (final NumberFormatException ignored) {
        console.println("  Invalid amount. Please enter a number.");
      }
    }
  }

  private Set<String> readSuppliers() {
    final String raw = console.readOptional("Supplier IDs (comma separated, blank keeps current): ");
    final Set<String> supplierIds = new LinkedHashSet<>();
    for (final String supplierId : raw.split(",")) {
      if (!supplierId.isBlank()) {
        supplierIds.add(supplierId.trim());
      }
    }
    return supplierIds;
  }
}
