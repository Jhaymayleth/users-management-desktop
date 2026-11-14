# Users Management Desktop

Aplicación Java 17 de escritorio que aplica arquitectura hexagonal y conceptos de DDD al CRUDL de
usuarios y al catálogo de productos del **CEA Ejercicio 28 — Lavadero**.

## Alcance del catálogo

`ProductModel` representa el agregado Producto. Sus Value Objects validan los datos y el agregado
mantiene la coherencia entre precios, inventario y estado. En la Unidad 2, el catálogo se persiste
en memoria; el adaptador PostgreSQL de la Unidad 3 aún está pendiente. La gestión existente de
usuarios conserva su adaptador MySQL.

## Arquitectura

- **Dominio:** `ProductModel`, Value Objects, estado y excepciones de negocio.
- **Aplicación:** casos de uso que orquestan validación y puertos de salida.
- **Infraestructura:** controlador y handlers CLI, adaptador de productos en memoria y adaptador
  MySQL de usuarios.
- **Configuración:** `DependencyContainer` conecta las capas sin que el dominio conozca la CLI ni
  las bases de datos.

Consulta el [diagrama y las reglas del producto](docs/product-architecture.md) y el
[lenguaje ubicuo](docs/lenguaje-ubicuo-producto.md).

## Ejecución y pruebas

Requisitos: JDK 17 y Maven.

```powershell
mvn test
mvn verify
```

Para iniciar la aplicación, ejecuta `com.jcaa.usersmanagement.Main` desde el IDE. La aplicación
requiere una conexión MySQL válida para el adaptador existente de usuarios.

## Configuración local

`src/main/resources/application.properties` contiene valores de ejemplo. Para credenciales locales,
crea `src/main/resources/application-local.properties`; sus claves sobrescriben las del archivo de
ejemplo. Ese archivo está ignorado por Git: no añadas contraseñas ni tokens a archivos versionados.
