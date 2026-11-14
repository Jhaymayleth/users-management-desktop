package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ProductResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.ProductController;
import lombok.RequiredArgsConstructor;

/** Handler del menú: listar los productos que están por agotarse. */
@RequiredArgsConstructor
public final class ListLowStockProductsHandler implements OperationHandler {

  private final ProductController productController;
  private final ProductResponsePrinter printer;

  @Override
  public void handle() {
    printer.printList(productController.listLowStockProducts());
  }
}
