# PostgreSQL local para Producto

El catálogo usa memoria por defecto. El adaptador PostgreSQL de Producto se selecciona con
`product.persistence=postgresql`; los usuarios continúan usando el adaptador MySQL existente.
La persistencia de Producto usa JDBC puro, sin ORM.

## Preparar la base de datos

Requiere PostgreSQL local, `psql`, `createdb` y `pg_dump` disponibles en `PATH`. Los comandos
solicitan la contraseña de PostgreSQL; no la escribas en archivos versionados.

En PowerShell, crea la base y aplica el esquema y los datos de prueba:

```powershell
createdb --host=127.0.0.1 --port=5432 --username=postgres lavadero
psql --host=127.0.0.1 --port=5432 --username=postgres --dbname=lavadero --file=db/postgresql/schema.sql
psql --host=127.0.0.1 --port=5432 --username=postgres --dbname=lavadero --file=db/postgresql/seed.sql
```

Los scripts crean la tabla `products`, la relación `product_suppliers`, sus restricciones e
índices. El seed es repetible para los identificadores de ejemplo.

## Activar PostgreSQL

Crea `src/main/resources/application-local.properties` y configura, con valores de tu equipo:

```properties
product.persistence=postgresql
product.db.host=127.0.0.1
product.db.port=5432
product.db.name=lavadero
product.db.username=postgres
product.db.password=<contraseña-local>
```

El archivo local está excluido de Git. No copies credenciales reales a `application.properties`,
pruebas, scripts versionados ni esta guía. La aplicación también requiere la configuración MySQL
local existente para el adaptador de usuarios.

## Ejecutar las pruebas

```powershell
mvn test
```

Las pruebas del adaptador PostgreSQL usan mocks JDBC. Además, el CRUDL completo se verificó contra
una instancia PostgreSQL 15 real: crear, consultar por identificador, actualizar, listar, buscar,
consultar stock bajo y eliminar.

## Respaldo incluido

El repositorio incluye `db/postgresql/backup.sql`, un dump en texto plano generado con `pg_dump`
15.3 desde una base `lavadero` creada con `schema.sql` y `seed.sql`. Se usaron `--no-owner` y
`--no-acl` para que el archivo se pueda restaurar con cualquier usuario local sin depender de roles
del equipo donde se generó:

```powershell
pg_dump --host=127.0.0.1 --port=5432 --username=postgres --format=plain --no-owner --no-acl --file=db/postgresql/backup.sql lavadero
```

Para regenerarlo después de cambiar los datos, ejecuta el mismo comando sobre la base actualizada.

## Restaurar el respaldo

```powershell
createdb --host=127.0.0.1 --port=5432 --username=postgres lavadero_restore
psql --host=127.0.0.1 --port=5432 --username=postgres --dbname=lavadero_restore --file=db/postgresql/backup.sql
```

El dump conserva la tabla `products`, la relación `product_suppliers`, sus restricciones, índices y
los tres productos del seed. La restauración se verificó comparando los conteos y un hash de las
filas de `products` entre la base original y la restaurada; ambos resultados coincidieron.
