-- Run as the schema owner after schema.sql, replacing INVENTORY_RUNTIME.
-- Grant only the DML and sequence access required by the application.
grant select on app_role to INVENTORY_RUNTIME;
grant select, insert, update on app_user to INVENTORY_RUNTIME;
grant select, insert, update, delete on category to INVENTORY_RUNTIME;
grant select, insert, update, delete on supplier to INVENTORY_RUNTIME;
grant select, insert, update, delete on product to INVENTORY_RUNTIME;
grant select, insert on stock_transaction to INVENTORY_RUNTIME;
grant select on role_seq to INVENTORY_RUNTIME;
grant select on user_seq to INVENTORY_RUNTIME;
grant select on category_seq to INVENTORY_RUNTIME;
grant select on supplier_seq to INVENTORY_RUNTIME;
grant select on product_seq to INVENTORY_RUNTIME;
grant select on stock_transaction_seq to INVENTORY_RUNTIME;
