-- Seed only role lookup data. The initial Admin is created interactively:
--   mvn -q -DskipTests package
--   java -cp "target/inventory-stock-manager-1.0.0.jar" edu.inventory.BootstrapAdmin
-- (Or run the bootstrap class from the IDE.) No default password is shipped.
insert into app_role(role_id,role_name) values(role_seq.nextval,'Admin');
insert into app_role(role_id,role_name) values(role_seq.nextval,'Inventory Staff');
insert into app_role(role_id,role_name) values(role_seq.nextval,'Viewer');
insert into app_role(role_id,role_name) values(role_seq.nextval,'Supplier');
commit;
