package com.jcaa.usersmanagement.domain.event;

import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import com.jcaa.usersmanagement.domain.valueobject.ProductStock;
import java.util.Map;
import lombok.Getter;

/** Evento publicado cuando el stock de un producto cambia por recepción o por venta. */
@Getter
public final class StockUpdatedDomainEvent extends DomainEvent {

  private static final String EVENT_NAME = "stock.updated";

  private final ProductId productId;
  private final ProductStock newStock;

  public StockUpdatedDomainEvent(final ProductId productId, final ProductStock newStock) {
    super(EVENT_NAME);
    this.productId = productId;
    this.newStock = newStock;
  }

  @Override
  public Map<String, String> payload() {
    return Map.of(
        "productId", productId.value(),
        "stock", newStock.toString());
  }
}
