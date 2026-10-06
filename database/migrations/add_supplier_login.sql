-- Apply once to an existing schema. Existing users remain unlinked and keep
-- their roles and current access.
alter table app_user add (supplier_id number(10));
alter table app_user add constraint app_user_supplier_fk foreign key (supplier_id) references supplier(supplier_id);
create index app_user_supplier_ix on app_user(supplier_id);

-- Admins link supplier users through the Users tab. The service requires an
-- active supplier and clears this reference for all other roles.
