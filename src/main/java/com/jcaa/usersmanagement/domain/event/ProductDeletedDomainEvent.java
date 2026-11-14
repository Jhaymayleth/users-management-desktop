package com.jcaa.usersmanagement.domain.event;

import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import java.util.Map;
import lombok.Getter;

/** Evento publicado cuando un producto se retira del catálogo. */
@Getter
public final class ProductDeletedDomainEvent extends DomainEvent {

  private static final String EVENT_NAME = "product.deleted";

  private final ProductId productId;

  public ProductDeletedDomainEvent(final ProductId productId) {
    super(EVENT_NAME);
    this.productId = productId;
  }

  @Override
  public Map<String, String> payload() {
    return Map.of("id", productId.value());
  }
}
