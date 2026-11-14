package com.jcaa.usersmanagement.domain.event;

import com.jcaa.usersmanagement.domain.valueobject.ProductId;
import com.jcaa.usersmanagement.domain.valueobject.ProductPrice;
import java.util.Map;
import lombok.Getter;

/** Evento publicado cuando se recalcula el precio de venta con la regla del 35 %. */
@Getter
public final class SalePriceRecalculatedDomainEvent extends DomainEvent {

  private static final String EVENT_NAME = "sale.price.recalculated";

  private final ProductId productId;
  private final ProductPrice previousSalePrice;
  private final ProductPrice newSalePrice;

  public SalePriceRecalculatedDomainEvent(
      final ProductId productId,
      final ProductPrice previousSalePrice,
      final ProductPrice newSalePrice) {
    super(EVENT_NAME);
    this.productId = productId;
    this.previousSalePrice = previousSalePrice;
    this.newSalePrice = newSalePrice;
  }

  @Override
  public Map<String, String> payload() {
    return Map.of(
        "productId", productId.value(),
        "previousSalePrice", previousSalePrice.toString(),
        "newSalePrice", newSalePrice.toString());
  }
}
