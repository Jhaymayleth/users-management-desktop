package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jcaa.usersmanagement.application.port.in.CreateProductUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteProductUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.GetLowStockProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.GetProductByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.SearchProductsUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateProductUseCase;
import com.jcaa.usersmanagement.application.service.dto.command.CreateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteProductCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateProductCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetProductByIdQuery;
import com.jcaa.usersmanagement.application.service.dto.query.SearchProductsQuery;
import com.jcaa.usersmanagement.domain.model.ProductModel;
import com.jcaa.usersmanagement.domain.valueobject.ProductBrand;
import com.jcaa.usersmanagement.domain.valueobject.ProductCategoryId;
import com.jcaa.usersmanagement.domain.valueobject.ProductCode;
import com.jcaa.usersmanagement.domain.valueobject.ProductDescription;
import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import com.jcaa.usersmanagement.domain.valueobject.ProductImageUrl;
import com.jcaa.usersmanagement.domain.valueobject.ProductMinimumStock;
import com.jcaa.usersmanagement.domain.valueobject.ProductName;
import com.jcaa.usersmanagement.domain.valueobject.ProductPrice;
import com.jcaa.usersmanagement.domain.valueobject.ProductStock;
import com.jcaa.usersmanagement.domain.valueobject.ProductSupplierId;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateProductRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.ProductResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateProductRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("ProductController")
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  private static final String CATEGORY_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String SUPPLIER_ID = "550e8400-e29b-41d4-a716-446655440101";

  @Mock private CreateProductUseCase createProductUseCase;
  @Mock private UpdateProductUseCase updateProductUseCase;
  @Mock private DeleteProductUseCase deleteProductUseCase;
  @Mock private GetProductByIdUseCase getProductByIdUseCase;
  @Mock private GetAllProductsUseCase getAllProductsUseCase;
  @Mock private SearchProductsUseCase searchProductsUseCase;
  @Mock private GetLowStockProductsUseCase getLowStockProductsUseCase;

  private ProductController controller;
  private ProductModel product;

  @BeforeEach
  void setUp() {
    controller =
        new ProductController(
            createProductUseCase,
            updateProductUseCase,
            deleteProductUseCase,
            getProductByIdUseCase,
            getAllProductsUseCase,
            searchProductsUseCase,
            getLowStockProductsUseCase);
    product =
        ProductModel.create(
            new ProductId("p-001"),
            new ProductCode("CODE-01"),
            new ProductName("Product A"),
            new ProductBrand("Brand A"),
            new ProductDescription("Product description"),
            new ProductPrice(new BigDecimal("100.00")),
            new ProductPrice(new BigDecimal("120.00")),
            new ProductStock(2),
            new ProductMinimumStock(5),
            new ProductCategoryId(CATEGORY_ID),
            Set.of(new ProductSupplierId(SUPPLIER_ID)),
            new ProductImageUrl("https://example.com/product.jpg"));
  }

  @Test
  @DisplayName("delegates create, read, list, search and low-stock requests")
  void shouldDelegateCatalogRequests() {
    when(createProductUseCase.execute(any())).thenReturn(product);
    when(updateProductUseCase.execute(any())).thenReturn(product);
    when(getProductByIdUseCase.execute(new GetProductByIdQuery("p-001"))).thenReturn(product);
    when(getAllProductsUseCase.execute()).thenReturn(List.of(product));
    when(searchProductsUseCase.execute(any())).thenReturn(List.of(product));
    when(getLowStockProductsUseCase.execute()).thenReturn(List.of(product));
    final CreateProductRequest request =
        new CreateProductRequest(
            "CODE-01",
            "Product A",
            "Brand A",
            "Product description",
            new BigDecimal("100.00"),
            new BigDecimal("120.00"),
            2,
            5,
            CATEGORY_ID,
            Set.of(SUPPLIER_ID),
            null);
    final ArgumentCaptor<CreateProductCommand> commandCaptor =
        ArgumentCaptor.forClass(CreateProductCommand.class);
    final UpdateProductRequest updateRequest =
        new UpdateProductRequest(
            "p-001",
            "Product A",
            "Brand A",
            "Product description",
            new BigDecimal("100.00"),
            new BigDecimal("120.00"),
            5,
            CATEGORY_ID,
            Set.of(SUPPLIER_ID),
            null);
    final ArgumentCaptor<UpdateProductCommand> updateCaptor =
        ArgumentCaptor.forClass(UpdateProductCommand.class);

    final ProductResponse created = controller.createProduct(request);
    final ProductResponse updated = controller.updateProduct(updateRequest);

    assertEquals("p-001", created.id());
    verify(createProductUseCase).execute(commandCaptor.capture());
    assertEquals(request.code(), commandCaptor.getValue().code());
    verify(updateProductUseCase).execute(updateCaptor.capture());
    assertEquals(updateRequest.id(), updateCaptor.getValue().id());
    assertEquals("p-001", updated.id());
    assertEquals("p-001", controller.findProductById("p-001").id());
    assertEquals(1, controller.listProducts().size());
    assertEquals(1, controller.searchProducts("Product", CATEGORY_ID, null, null).size());
    assertEquals(1, controller.listLowStockProducts().size());
    verify(searchProductsUseCase)
        .execute(new SearchProductsQuery("Product", CATEGORY_ID, null, null));
  }

  @Test
  @DisplayName("delegates product deletion by identifier")
  void shouldDelegateDelete() {
    final ArgumentCaptor<DeleteProductCommand> commandCaptor =
        ArgumentCaptor.forClass(DeleteProductCommand.class);

    controller.deleteProduct("p-001");

    verify(deleteProductUseCase).execute(commandCaptor.capture());
    assertEquals("p-001", commandCaptor.getValue().id());
  }
}
