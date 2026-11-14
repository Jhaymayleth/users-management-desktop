package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.InvalidProductException;
import com.jcaa.usersmanagement.domain.exception.ProductAlreadyExistsException;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ProductResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.ProductController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateProductRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.ProductResponse;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * Handler del menú: registrar un producto en el catálogo.
 *
 * <p>Lee los datos de la consola, los empaqueta en un DTO de entrada y delega en el controlador.
 * Los errores de negocio se muestran sin traza técnica.
 */
@RequiredArgsConstructor
public final class CreateProductHandler implements OperationHandler {

  private final ProductController productController;
  private final ConsoleIO console;
  private final ProductResponsePrinter printer;

  @Override
  public void handle() {
    final String code = console.readRequired("Code            : ");
    final String name = console.readRequired("Name            : ");
    final String brand = console.readOptional("Brand (optional): ");
    final String description = console.readRequired("Description     : ");
    final BigDecimal purchasePrice = readDecimal("Purchase price  : ");
    final BigDecimal salePrice = readDecimal("Sale price      : ");
    final int stock = console.readInt("Initial stock   : ");
    final int minimumStock = console.readInt("Minimum stock   : ");
    final String categoryId = console.readRequired("Category ID     : ");
    final Set<String> supplierIds = readSuppliers();
    final String imageUrl = console.readOptional("Image URL       : ");

    final CreateProductRequest request =
        new CreateProductRequest(
            code,
            name,
            brand,
            description,
            purchasePrice,
            salePrice,
            stock,
            minimumStock,
            categoryId,
            supplierIds,
            imageUrl);

    try {
      final ProductResponse created = productController.createProduct(request);
      console.println("\n  Product created successfully.");
      printer.print(created);
    } catch (final ProductAlreadyExistsException | InvalidProductException exception) {
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
    final String raw = console.readRequired("Supplier IDs (comma separated): ");
    final Set<String> supplierIds = new LinkedHashSet<>();
    for (final String supplierId : raw.split(",")) {
      if (!supplierId.isBlank()) {
        supplierIds.add(supplierId.trim());
      }
    }
    return supplierIds;
  }
}
