-- Run as the INVENTORY_APP schema owner in the target Oracle PDB.
-- Oracle 12.2+ recommended. This script is for a new, empty schema.
create sequence role_seq start with 1 increment by 1;
create sequence user_seq start with 1 increment by 1;
create sequence category_seq start with 1 increment by 1;
create sequence supplier_seq start with 1 increment by 1;
create sequence product_seq start with 1 increment by 1;
create sequence stock_transaction_seq start with 1 increment by 1;

create table app_role (
  role_id number(10) primary key,
  role_name varchar2(30 char) not null unique
);
create table app_user (
  user_id number(10) primary key,
  username varchar2(60 char) not null,
  password_hash varchar2(300 char) not null,
  role_id number(10) not null references app_role(role_id),
  active char(1 char) default 'Y' not null check (active in ('Y','N')),
  created_at timestamp default systimestamp not null
);
create unique index app_user_username_uq on app_user(upper(username));

create table category (
  category_id number(10) primary key,
  category_name varchar2(100 char) not null,
  description varchar2(500 char),
  active char(1 char) default 'Y' not null check (active in ('Y','N'))
);
create unique index category_name_uq on category(upper(category_name));

create table supplier (
  supplier_id number(10) primary key,
  supplier_name varchar2(150 char) not null,
  contact_name varchar2(120 char),
  email varchar2(254 char),
  phone varchar2(40 char),
  address varchar2(500 char),
  active char(1 char) default 'Y' not null check (active in ('Y','N'))
);
create unique index supplier_name_uq on supplier(upper(supplier_name));

create table product (
  product_id number(10) primary key,
  sku varchar2(40 char) not null,
  product_name varchar2(120 char) not null,
  description varchar2(1000 char),
  category_id number(10) not null references category(category_id),
  supplier_id number(10) not null references supplier(supplier_id),
  unit_of_measure varchar2(30 char) not null,
  reorder_level number(14,3) default 0 not null check (reorder_level >= 0),
  active char(1 char) default 'Y' not null check (active in ('Y','N'))
);
create unique index product_sku_uq on product(upper(sku));

create table stock_transaction (
  transaction_id number(12) primary key,
  product_id number(10) not null references product(product_id),
  quantity number(14,3) not null check (quantity > 0),
  transaction_type varchar2(3 char) not null check (transaction_type in ('IN','OUT')),
  occurred_at timestamp default systimestamp not null,
  user_id number(10) not null references app_user(user_id),
  reference varchar2(200 char),
  note varchar2(1000 char)
);
create index stock_tx_product_time_ix on stock_transaction(product_id,occurred_at);
create index stock_tx_user_time_ix on stock_transaction(user_id,occurred_at);

-- Reserved Supplier role is seeded for schema compatibility, but supplier login is disabled in v1.
