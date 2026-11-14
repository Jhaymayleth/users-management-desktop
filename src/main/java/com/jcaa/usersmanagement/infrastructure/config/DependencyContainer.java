package com.jcaa.usersmanagement.infrastructure.config;

import com.jcaa.usersmanagement.application.port.in.CreateProductUseCase;
import com.jcaa.usersmanagement.application.port.in.CreateUserUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteProductUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteUserUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllUsersUseCase;
import com.jcaa.usersmanagement.application.port.in.GetLowStockProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.GetProductByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.GetUserByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.LoginUseCase;
import com.jcaa.usersmanagement.application.port.in.SearchProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateProductUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateUserUseCase;
import com.jcaa.usersmanagement.application.port.out.DeleteProductPort;
import com.jcaa.usersmanagement.application.port.out.GetAllProductsPort;
import com.jcaa.usersmanagement.application.port.out.GetLowStockProductsPort;
import com.jcaa.usersmanagement.application.port.out.GetProductByCodePort;
import com.jcaa.usersmanagement.application.port.out.GetProductByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveProductPort;
import com.jcaa.usersmanagement.application.port.out.SearchProductsPort;
import com.jcaa.usersmanagement.application.port.out.UpdateProductPort;
import com.jcaa.usersmanagement.application.service.CreateProductService;
import com.jcaa.usersmanagement.application.service.CreateUserService;
import com.jcaa.usersmanagement.application.service.DeleteProductService;
import com.jcaa.usersmanagement.application.service.DeleteUserService;
import com.jcaa.usersmanagement.application.service.EmailNotificationService;
import com.jcaa.usersmanagement.application.service.GetAllProductsService;
import com.jcaa.usersmanagement.application.service.GetAllUsersService;
import com.jcaa.usersmanagement.application.service.GetLowStockProductsService;
import com.jcaa.usersmanagement.application.service.GetProductByIdService;
import com.jcaa.usersmanagement.application.service.GetUserByIdService;
import com.jcaa.usersmanagement.application.service.LoginService;
import com.jcaa.usersmanagement.application.service.SearchProductsService;
import com.jcaa.usersmanagement.application.service.UpdateProductService;
import com.jcaa.usersmanagement.application.service.UpdateUserService;
import com.jcaa.usersmanagement.infrastructure.adapter.email.JavaMailEmailSenderAdapter;
import com.jcaa.usersmanagement.infrastructure.adapter.email.SmtpConfig;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.config.DatabaseConfig;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.config.DatabaseConnectionFactory;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.config.PostgresDatabaseConfig;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.config.PostgresDatabaseConnectionFactory;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository.InMemoryProductRepositoryAdapter;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository.PostgresProductRepositoryAdapter;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository.UserRepositoryMySQL;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.ProductController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;

import java.sql.Connection;
import jakarta.validation.Validator;

public final class DependencyContainer {

  private static final String DB_HOST = "db.host";
  private static final String DB_PORT = "db.port";
  private static final String DB_NAME = "db.name";
  private static final String DB_USER = "db.username";
  private static final String DB_PASSWORD = "db.password";

  private static final String PRODUCT_PERSISTENCE = "product.persistence";
  private static final String PRODUCT_DB_HOST = "product.db.host";
  private static final String PRODUCT_DB_PORT = "product.db.port";
  private static final String PRODUCT_DB_NAME = "product.db.name";
  private static final String PRODUCT_DB_USER = "product.db.username";
  private static final String PRODUCT_DB_PASSWORD = "product.db.password";

  private static final String SMTP_HOST = "smtp.host";
  private static final String SMTP_PORT = "smtp.port";
  private static final String SMTP_USER = "smtp.username";
  private static final String SMTP_PASSWORD = "smtp.password";
  private static final String SMTP_FROM = "smtp.from.address";
  private static final String SMTP_FROM_NAME = "smtp.from.name";

  private final UserController userController;
  private final ProductController productController;

  public DependencyContainer() {
    final AppProperties properties = new AppProperties();

    final Connection connection = buildDatabaseConnection(properties);
    final UserRepositoryMySQL userRepository = new UserRepositoryMySQL(connection);

    final JavaMailEmailSenderAdapter emailSender =
        new JavaMailEmailSenderAdapter(buildSmtpConfig(properties));
    final EmailNotificationService emailNotification = new EmailNotificationService(emailSender);

    // Construir Validator para las validaciones en la capa de aplicación
    final Validator validator = ValidatorProvider.buildValidator();

    final CreateUserUseCase createUserUseCase =
        new CreateUserService(userRepository, userRepository, emailNotification, validator);
    final UpdateUserUseCase updateUserUseCase =
        new UpdateUserService(userRepository, userRepository, userRepository, emailNotification, validator);
    final DeleteUserUseCase deleteUserUseCase =
        new DeleteUserService(userRepository, userRepository, validator);
    final GetUserByIdUseCase getUserByIdUseCase = new GetUserByIdService(userRepository, validator);
    final GetAllUsersUseCase getAllUsersUseCase = new GetAllUsersService(userRepository);
    final LoginUseCase loginUseCase = new LoginService(userRepository, validator);

    this.userController =
        new UserController(
            createUserUseCase,
            updateUserUseCase,
            deleteUserUseCase,
            getUserByIdUseCase,
            getAllUsersUseCase,
            loginUseCase);

    final SaveProductPort saveProductPort;
    final UpdateProductPort updateProductPort;
    final DeleteProductPort deleteProductPort;
    final GetProductByIdPort getProductByIdPort;
    final GetProductByCodePort getProductByCodePort;
    final GetAllProductsPort getAllProductsPort;
    final SearchProductsPort searchProductsPort;
    final GetLowStockProductsPort getLowStockProductsPort;
    final String productPersistence = properties.get(PRODUCT_PERSISTENCE);
    if ("memory".equalsIgnoreCase(productPersistence)) {
      final InMemoryProductRepositoryAdapter productRepository =
          new InMemoryProductRepositoryAdapter();
      saveProductPort = productRepository;
      updateProductPort = productRepository;
      deleteProductPort = productRepository;
      getProductByIdPort = productRepository;
      getProductByCodePort = productRepository;
      getAllProductsPort = productRepository;
      searchProductsPort = productRepository;
      getLowStockProductsPort = productRepository;
    } else if ("postgresql".equalsIgnoreCase(productPersistence)) {
      final PostgresDatabaseConfig productDatabaseConfig =
          new PostgresDatabaseConfig(
              properties.get(PRODUCT_DB_HOST),
              properties.getInt(PRODUCT_DB_PORT),
              properties.get(PRODUCT_DB_NAME),
              properties.get(PRODUCT_DB_USER),
              properties.get(PRODUCT_DB_PASSWORD));
      final PostgresProductRepositoryAdapter productRepository =
          new PostgresProductRepositoryAdapter(
              PostgresDatabaseConnectionFactory.createConnection(productDatabaseConfig));
      saveProductPort = productRepository;
      updateProductPort = productRepository;
      deleteProductPort = productRepository;
      getProductByIdPort = productRepository;
      getProductByCodePort = productRepository;
      getAllProductsPort = productRepository;
      searchProductsPort = productRepository;
      getLowStockProductsPort = productRepository;
    } else {
      throw new IllegalStateException(
          "Unsupported product persistence mode: " + productPersistence);
    }

    final CreateProductUseCase createProductUseCase =
        new CreateProductService(saveProductPort, getProductByCodePort, validator);
    final UpdateProductUseCase updateProductUseCase =
        new UpdateProductService(getProductByIdPort, updateProductPort, validator);
    final DeleteProductUseCase deleteProductUseCase =
        new DeleteProductService(getProductByIdPort, deleteProductPort, validator);
    final GetProductByIdUseCase getProductByIdUseCase =
        new GetProductByIdService(getProductByIdPort, validator);
    final GetAllProductsUseCase getAllProductsUseCase =
        new GetAllProductsService(getAllProductsPort);
    final SearchProductsUseCase searchProductsUseCase =
        new SearchProductsService(searchProductsPort);
    final GetLowStockProductsUseCase getLowStockProductsUseCase =
        new GetLowStockProductsService(getLowStockProductsPort);

    this.productController =
        new ProductController(
            createProductUseCase,
            updateProductUseCase,
            deleteProductUseCase,
            getProductByIdUseCase,
            getAllProductsUseCase,
            searchProductsUseCase,
            getLowStockProductsUseCase);
  }

  public UserController userController() {
    return userController;
  }

  public ProductController productController() {
    return productController;
  }

  private static Connection buildDatabaseConnection(final AppProperties properties) {
    final DatabaseConfig config =
        new DatabaseConfig(
            properties.get(DB_HOST),
            properties.getInt(DB_PORT),
            properties.get(DB_NAME),
            properties.get(DB_USER),
            properties.get(DB_PASSWORD));
    return DatabaseConnectionFactory.createConnection(config);
  }

  private static SmtpConfig buildSmtpConfig(final AppProperties properties) {
    return new SmtpConfig(
        properties.get(SMTP_HOST),
        properties.getInt(SMTP_PORT),
        properties.get(SMTP_USER),
        properties.get(SMTP_PASSWORD),
        properties.get(SMTP_FROM),
        properties.get(SMTP_FROM_NAME));
  }
}
