# Inventory & Stock Manager

A desktop inventory system built with Java Swing, JDBC, and Oracle Database. The initial version includes authenticated user accounts, role-based access, product/category/supplier maintenance, stock movement recording, and stock/reconciliation reports.

## Prerequisites

- Java 17 or newer (the project was set to compile against Java 17).
- Apache Maven 3.9 or newer to build and retrieve the Oracle JDBC driver.
- Oracle Database 19c or newer, with a PDB/service name reachable from the application host.
- A graphical desktop session to run the Swing application.

## Oracle setup

Use a dedicated schema for this application. For local development, the schema owner can also run the app. For a restricted runtime account, create an owner and runtime user, grant `CREATE SESSION` to the runtime user, run `database/runtime_grants.sql` as the owner (substitute the runtime username), and configure `db.schema` to the owner name. The runtime account then has only the DML and sequence access used by the app. Keep the owner account out of the application configuration.

Connect as the schema owner in SQL*Plus, SQLcl, or SQL Developer and run these scripts in order on a **new, empty schema**:

1. `database/schema.sql` — tables, sequences, keys, indexes, and constraints.
2. `database/seed.sql` — role lookup rows only; it creates no sample inventory records or passwords.
3. For a separate runtime user, run `database/runtime_grants.sql` after replacing `INVENTORY_RUNTIME` with the runtime username.

The schema uses one role per user and one primary supplier per product. Transaction rows contain IDs and movement details only; product and user descriptions remain normalized in their own tables. Stock is calculated from positive `IN` and `OUT` movements. No sales, scheduling, or payment tables are present.

## Configuration

Copy `config/application.properties.example` to `config/application.properties` and fill in the Oracle connection URL, username, and password. The real configuration file is ignored by Git.

```properties
db.url=jdbc:oracle:thin:@//localhost:1521/FREEPDB1
db.username=inventory_runtime
db.password=use-a-secret-from-your-local-environment
db.schema=INVENTORY_OWNER
```

Instead of a file, supply `INVENTORY_DB_URL`, `INVENTORY_DB_USERNAME`, and `INVENTORY_DB_PASSWORD` environment variables. `INVENTORY_DB_SCHEMA` optionally selects the Oracle schema owner when the runtime account has restricted grants. Environment variables take precedence. Do not commit real credentials.

## Build and run

From the project root:

```powershell
mvn package
```

The shaded jar contains the Oracle JDBC driver. Build the schema and seed roles before bootstrapping the first Admin. Run the bootstrap from a terminal (so password input is not echoed):

```powershell
java -cp target/inventory-stock-manager-1.0.0.jar edu.inventory.BootstrapAdmin
```

Choose an Admin username and a password with at least 10 characters at the prompts. No default account or plaintext password is shipped. Then launch the UI:

```powershell
java -jar target/inventory-stock-manager-1.0.0.jar
```

The same entry points can be run from an IDE by selecting `edu.inventory.BootstrapAdmin` or `edu.inventory.Main` as the main class. Maven must resolve `com.oracle.database.jdbc:ojdbc11`; Java compilation itself has no non-JDK dependencies.

## Roles and permissions

| Capability | Admin | Inventory Staff | Viewer | Supplier (reserved) |
|---|---:|---:|---:|---:|
| View/search products, current stock, and movement history | Yes | Yes | Yes | No account in v1 |
| Add/update products | Yes | Yes | No | No account in v1 |
| Deactivate/delete products | Yes | No | No | No account in v1 |
| Maintain categories and suppliers | Yes | No | No | No account in v1 |
| Record stock-in/out | Yes | Yes | No | No account in v1 |
| Create/update/disable users and assign roles | Yes | No | No | No account in v1 |

Supplier is seeded only as a reserved role value. Supplier accounts and portal access are not implemented. Role checks are enforced in the service layer and the account's active role is rechecked for service operations, so changes take effect without trusting hidden UI controls.

## Implemented features

- Asynchronous Swing login and data operations to keep the UI responsive.
- Product create/update/delete (delete only when no transaction history), search, and Admin deactivation.
- Category and supplier create/update/searchable lists and activate/deactivate controls for Admins.
- Admin user creation, role assignment, update, password reset, and disable controls.
- Positive-quantity stock-in and stock-out entries with database timestamp, user, type, and optional reference/note.
- Product-row locking (`SELECT ... FOR UPDATE`) around stock validation and insertion to serialize competing movements and prevent negative stock.
- Current-stock report with product/category/supplier, unit, reorder threshold, and low-stock indicator; searchable by SKU/name/category/supplier.
- Transaction-history report filtered by date range, product, user, and movement type.
- PBKDF2-HMAC-SHA256 password hashes with random per-password salt; parameterized SQL and Oracle integrity constraints.

## Architecture and source layout

```text
edu.inventory.ui       Swing login and screens
edu.inventory.service  Validation, authorization, stock rules
edu.inventory.dao      Parameterized JDBC queries and transactions
edu.inventory.model    Session, roles, and data records
edu.inventory.config   Oracle connection configuration
edu.inventory.security Password hashing
database/              Oracle schema, role seed, restricted grants
```

Completed stock transactions cannot be edited or deleted in the UI or through the runtime account. Correction/adjustment workflows are future work and would require an explicit movement type and authorization policy.

## Known limitations and future work

- Oracle itself is required for persistence; the application does not include an embedded or mock database.
- Product quantity is derived from all movement rows; very large histories may later need a carefully maintained Oracle summary/materialized view for performance.
- Password reset is Admin-operated; self-service recovery and lockout policy are not included.
- Sales reports need sales/order/receipt data and prices; stock-outs alone do not calculate revenue.
- Supplier login, supplier scheduling, due/payment tracking, and purchase-order accounting are intentionally out of scope.
- Transaction correction/adjustment entries are not implemented in v1.
