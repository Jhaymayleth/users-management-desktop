package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.ProductNotFoundException;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ProductResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.ProductController;
import lombok.RequiredArgsConstructor;

/** Handler del menú: consultar un producto por su identificador. */
@RequiredArgsConstructor
public final class FindProductByIdHandler implements OperationHandler {

  private final ProductController productController;
  private final ConsoleIO console;
  private final ProductResponsePrinter printer;

  @Override
  public void handle() {
    final String id = console.readRequired("Product ID : ");
    try {
      printer.print(productController.findProductById(id));
    } catch (final ProductNotFoundException exception) {
      console.println("  Error: " + exception.getMessage());
    }
  }
}
