package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli;

import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.CreateProductHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.CreateUserHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.DeleteProductHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.DeleteUserHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.FindProductByIdHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.FindUserByIdHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.ListLowStockProductsHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.ListProductsHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.ListUsersHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.LoginHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.OperationHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.UpdateProductHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.UpdateUserHandler;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ProductResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.UserResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.menu.MenuOption;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.ProductController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import jakarta.validation.ConstraintViolationException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Adaptador de entrada de la aplicación de escritorio.
 *
 * <p>Muestra el menú, delega cada opción en su handler y traduce los errores de validación a
 * mensajes legibles. El menú incluye los casos de uso de usuarios y los del catálogo de productos.
 */
@RequiredArgsConstructor
public final class UserManagementCli {

  private static final String BANNER =
      """
      ==========================================
           Users Management System
      ==========================================""";

  private static final String MENU_BORDER = "  ==========================================";

  private final UserController userController;
  private final ProductController productController;
  private final ConsoleIO console;

  public void start() {
    console.println(BANNER);
    final UserResponsePrinter userPrinter = new UserResponsePrinter(console);
    final ProductResponsePrinter productPrinter = new ProductResponsePrinter(console);
    runLoop(buildHandlers(userPrinter, productPrinter));
  }

  private void runLoop(final Map<MenuOption, OperationHandler> handlers) {
    boolean running = true;
    while (running) {
      printMenu();
      final int choice = console.readInt("\n  Option: ");
      final Optional<MenuOption> option = MenuOption.fromNumber(choice);

      if (option.isEmpty()) {
        console.println("  Invalid option. Please try again.");
      } else if (option.get() == MenuOption.EXIT) {
        console.println("\n  Goodbye!\n");
        running = false;
      } else {
        executeHandler(handlers, option.get());
      }
    }
  }

  private void executeHandler(
      final Map<MenuOption, OperationHandler> handlers, final MenuOption option) {
    try {
      handlers.get(option).handle();
    } catch (final ConstraintViolationException exception) {
      console.println("  Validation errors:");
      exception.getConstraintViolations()
          .forEach(violation -> console.println("    - " + violation.getMessage()));
    } catch (final RuntimeException exception) {
      console.println("  Unexpected error: " + exception.getMessage());
    }
  }

  private Map<MenuOption, OperationHandler> buildHandlers(
      final UserResponsePrinter userPrinter, final ProductResponsePrinter productPrinter) {
    final Map<MenuOption, OperationHandler> handlers = new EnumMap<>(MenuOption.class);

    handlers.put(MenuOption.LIST_USERS, new ListUsersHandler(userController, userPrinter));
    handlers.put(
        MenuOption.FIND_USER, new FindUserByIdHandler(userController, console, userPrinter));
    handlers.put(MenuOption.CREATE_USER, new CreateUserHandler(userController, console, userPrinter));
    handlers.put(MenuOption.UPDATE_USER, new UpdateUserHandler(userController, console, userPrinter));
    handlers.put(MenuOption.DELETE_USER, new DeleteUserHandler(userController, console));
    handlers.put(MenuOption.LOGIN, new LoginHandler(userController, console, userPrinter));

    handlers.put(MenuOption.LIST_PRODUCTS, new ListProductsHandler(productController, productPrinter));
    handlers.put(
        MenuOption.FIND_PRODUCT,
        new FindProductByIdHandler(productController, console, productPrinter));
    handlers.put(
        MenuOption.CREATE_PRODUCT, new CreateProductHandler(productController, console, productPrinter));
    handlers.put(
        MenuOption.UPDATE_PRODUCT, new UpdateProductHandler(productController, console, productPrinter));
    handlers.put(MenuOption.DELETE_PRODUCT, new DeleteProductHandler(productController, console));
    handlers.put(
        MenuOption.LOW_STOCK_PRODUCTS,
        new ListLowStockProductsHandler(productController, productPrinter));

    return handlers;
  }

  private void printMenu() {
    console.println();
    console.println(MENU_BORDER);
    console.println("    Main Menu");
    console.println(MENU_BORDER);
    for (final MenuOption option : MenuOption.values()) {
      console.printf("    [%d] %s%n", option.getNumber(), option.getDescription());
    }
    console.println(MENU_BORDER);
  }
}
