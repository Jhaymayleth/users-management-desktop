package com.jcaa.usersmanagement.domain.event;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.Map;
import lombok.Getter;

/** Evento publicado cuando cambian los datos editables de un producto. */
@Getter
public final class ProductUpdatedDomainEvent extends DomainEvent {

  private static final String EVENT_NAME = "product.updated";

  private final ProductModel product;

  public ProductUpdatedDomainEvent(final ProductModel product) {
    super(EVENT_NAME);
    this.product = product;
  }

  @Override
  public Map<String, String> payload() {
    return Map.of(
        "id", product.getId().value(),
        "code", product.getCode().value(),
        "name", product.getName().value(),
        "salePrice", product.getSalePrice().toString(),
        "status", product.getStatus().name());
  }
}
