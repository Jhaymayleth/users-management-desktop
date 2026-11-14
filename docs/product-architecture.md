# Arquitectura del catálogo de productos

El catálogo del CEA **Ejercicio 28 — Lavadero** se organiza en capas. Las dependencias apuntan hacia
el dominio; la CLI y la persistencia son adaptadores reemplazables.

## Diagrama de componentes

```text
┌──────────────────────────────────────────┐
│ Adaptador de entrada                     │
│ UserManagementCli → ProductController    │
│ handlers, requests y responses           │
└────────────────────┬─────────────────────┘
                     │ llama
                     ▼
┌──────────────────────────────────────────┐
│ Aplicación                               │
│ puertos IN → casos de uso                │
│ comandos, consultas y ProductApplicationMapper
└────────────────────┬─────────────────────┘
                     │ usa
                     ▼
┌──────────────────────────────────────────┐
│ Dominio                                  │
│ ProductModel (raíz)                     │
│ ProductCode, ProductPrice, ProductStock │
│ ProductStatus y excepciones             │
└──────────────────────────────────────────┘
                     ▲
                     │ implementa
┌────────────────────┴─────────────────────┐
│ Adaptador de salida                      │
│ puertos OUT → InMemoryProductRepositoryAdapter │
└──────────────────────────────────────────┘
```

`DependencyContainer` conecta los puertos con sus implementaciones. El dominio y los casos de uso
no dependen de la consola ni de `ConcurrentHashMap`.

## Invariantes principales

- Código y nombre válidos; precio de compra y precio de venta positivos.
- Precio de venta no menor que el precio de compra.
- Stock no negativo y stock mínimo no negativo.
- Categoría y proveedores referenciados por identificadores UUID.
- Estado coherente con el inventario: `OUT_OF_STOCK`, `LOW_STOCK` o `AVAILABLE`; un producto
  `DISCONTINUED` permanece descontinuado.
- La venta no puede exceder las unidades disponibles.

`ProductModel` es inmutable: las operaciones de negocio devuelven una copia con la identidad
preservada. El adaptador en memoria ordena el catálogo por nombre y permite búsquedas por texto,
categoría, estado y marca.

## Flujo de registro

```text
CLI → ProductController → CreateProductUseCase
    → CreateProductService → ProductApplicationMapper
    → ProductModel.create → SaveProductPort
    → InMemoryProductRepositoryAdapter
```

El controlador traduce DTOs de frontera; el servicio valida el comando y la unicidad del código;
el mapper construye Value Objects, y el adaptador implementa la persistencia.

## Persistencia por unidad

| Unidad | Adaptador de Producto | Adaptador de Usuario |
|---|---|---|
| 2 | Memoria (`InMemoryProductRepositoryAdapter`) | MySQL |
| 3 | PostgreSQL con JDBC (pendiente) | MySQL |

El cambio de adaptador de Producto se realizará en el cableado, conservando los mismos puertos de
salida. No se usa ORM.
