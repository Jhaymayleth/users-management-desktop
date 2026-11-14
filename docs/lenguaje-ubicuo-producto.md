# Lenguaje ubicuo del catálogo

| Término del lavadero | Significado en el negocio | Nombre en código |
|---|---|---|
| Producto | Artículo ofrecido por el lavadero y mantenido en el catálogo | `ProductModel` |
| Código de producto | Identificador legible y único en el catálogo | `ProductCode` |
| Precio de compra | Costo unitario pagado al proveedor | `ProductPrice` (`purchasePrice`) |
| Precio de venta | Valor unitario cobrado al cliente | `ProductPrice` (`salePrice`) |
| Existencias / stock | Unidades disponibles en bodega | `ProductStock` |
| Stock mínimo | Umbral que alerta que hay que reponer | `ProductMinimumStock` |
| Producto por agotarse | Producto cuyo stock es igual o menor al mínimo | `ProductModel.isLowStock()` |
| Categoría | Clasificación del producto en el catálogo | `ProductCategoryId` |
| Proveedor | Tercero que suministra el producto | `ProductSupplierId` |
| Descontinuado | Producto que ya no se repone ni se ofrece | `ProductStatus.DISCONTINUED` |
| Recibir mercancía | Aumentar las unidades tras aceptar un pedido | `ProductModel.receiveStock(units)` |
| Vender producto | Descontar las unidades entregadas al cliente | `ProductModel.sell(units)` |

Los nombres de clases y paquetes se mantienen en inglés, siguiendo el repositorio existente. Las
reglas, pantallas y explicaciones del negocio usan los términos en español de esta tabla.
