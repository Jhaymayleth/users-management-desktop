package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ProductResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.ProductController;
import lombok.RequiredArgsConstructor;

/** Handler del menú: listar el catálogo completo. */
@RequiredArgsConstructor
public final class ListProductsHandler implements OperationHandler {

  private final ProductController productController;
  private final ProductResponsePrinter printer;

  @Override
  public void handle() {
    printer.printList(productController.listProducts());
  }
}
