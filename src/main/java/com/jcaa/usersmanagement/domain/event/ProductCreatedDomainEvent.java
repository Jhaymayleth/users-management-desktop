package com.jcaa.usersmanagement.domain.event;

import com.jcaa.usersmanagement.domain.model.ProductModel;
import java.util.Map;
import lombok.Getter;

/** Evento publicado cuando un producto se registra en el catálogo. */
@Getter
public final class ProductCreatedDomainEvent extends DomainEvent {

  private static final String EVENT_NAME = "product.created";

  private final ProductModel product;

  public ProductCreatedDomainEvent(final ProductModel product) {
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
