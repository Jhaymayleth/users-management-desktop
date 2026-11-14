package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io;

import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.ProductResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;

/** Presenta los productos del catálogo en la consola con un formato estable. */
@RequiredArgsConstructor
public final class ProductResponsePrinter {

  private static final String SEPARATOR = "-".repeat(52);
  private static final String ROW_FORMAT = "  %-12s : %s%n";

  private final ConsoleIO console;

  public void print(final ProductResponse response) {
    console.println(SEPARATOR);
    console.printf(ROW_FORMAT, "ID", response.id());
    console.printf(ROW_FORMAT, "Code", response.code());
    console.printf(ROW_FORMAT, "Name", response.name());
    console.printf(ROW_FORMAT, "Brand", response.brand());
    console.printf(ROW_FORMAT, "Description", response.description());
    console.printf(ROW_FORMAT, "Purchase", response.purchasePrice());
    console.printf(ROW_FORMAT, "Sale", response.salePrice());
    console.printf(ROW_FORMAT, "Stock", response.stock());
    console.printf(ROW_FORMAT, "Minimum", response.minimumStock());
    console.printf(ROW_FORMAT, "Category", response.categoryId());
    console.printf(ROW_FORMAT, "Suppliers", response.supplierIds());
    console.printf(ROW_FORMAT, "Status", response.status());
    console.printf(ROW_FORMAT, "Low stock", response.lowStock() ? "YES" : "NO");
    console.println(SEPARATOR);
  }

  public void printList(final List<ProductResponse> products) {
    if (products.isEmpty()) {
      console.println("  No products found.");
      return;
    }
    console.printf("%n  Total: %d product(s)%n", products.size());
    products.forEach(this::print);
  }
}
