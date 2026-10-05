-- Add demonstration inventory to an existing Oracle schema.
-- Run as the schema owner after database/schema.sql, database/seed.sql,
-- and creating at least one active Admin account.
-- Safe to rerun: existing matching categories, suppliers, products, and
-- sample opening transactions are left in place.

SET DEFINE OFF;

MERGE INTO category c
USING (SELECT 'Electronics' category_name, 'Demo electronic accessories' description FROM dual) s
ON (UPPER(c.category_name) = UPPER(s.category_name))
WHEN NOT MATCHED THEN
  INSERT (category_id, category_name, description, active)
  VALUES (category_seq.NEXTVAL, s.category_name, s.description, 'Y');

MERGE INTO category c
USING (SELECT 'Office Supplies' category_name, 'Demo office consumables' description FROM dual) s
ON (UPPER(c.category_name) = UPPER(s.category_name))
WHEN NOT MATCHED THEN
  INSERT (category_id, category_name, description, active)
  VALUES (category_seq.NEXTVAL, s.category_name, s.description, 'Y');

MERGE INTO category c
USING (SELECT 'Safety Equipment' category_name, 'Demo personal protective equipment' description FROM dual) s
ON (UPPER(c.category_name) = UPPER(s.category_name))
WHEN NOT MATCHED THEN
  INSERT (category_id, category_name, description, active)
  VALUES (category_seq.NEXTVAL, s.category_name, s.description, 'Y');

MERGE INTO supplier s
USING (SELECT 'Northstar Office & Tech' supplier_name, 'Demo supplier' contact_name,
              'northstar@example.test' email, '000-555-0101' phone,
              'Sample address' address FROM dual) x
ON (UPPER(s.supplier_name) = UPPER(x.supplier_name))
WHEN NOT MATCHED THEN
  INSERT (supplier_id, supplier_name, contact_name, email, phone, address, active)
  VALUES (supplier_seq.NEXTVAL, x.supplier_name, x.contact_name, x.email, x.phone, x.address, 'Y');

MERGE INTO supplier s
USING (SELECT 'Harbor Safety Supply' supplier_name, 'Demo supplier' contact_name,
              'harbor@example.test' email, '000-555-0102' phone,
              'Sample address' address FROM dual) x
ON (UPPER(s.supplier_name) = UPPER(x.supplier_name))
WHEN NOT MATCHED THEN
  INSERT (supplier_id, supplier_name, contact_name, email, phone, address, active)
  VALUES (supplier_seq.NEXTVAL, x.supplier_name, x.contact_name, x.email, x.phone, x.address, 'Y');

MERGE INTO product p
USING (
  SELECT 'ACC-MOU-001' sku, 'Wireless Mouse' product_name,
         'Demo USB wireless mouse' description, 'Electronics' category_name,
         'Northstar Office & Tech' supplier_name, 'each' unit_of_measure, 8 reorder_level FROM dual
) x
ON (UPPER(p.sku) = UPPER(x.sku))
WHEN NOT MATCHED THEN INSERT
  (product_id, sku, product_name, description, category_id, supplier_id, unit_of_measure, reorder_level, active)
  VALUES (product_seq.NEXTVAL, x.sku, x.product_name, x.description,
          (SELECT category_id FROM category WHERE UPPER(category_name) = UPPER(x.category_name)),
          (SELECT supplier_id FROM supplier WHERE UPPER(supplier_name) = UPPER(x.supplier_name)),
          x.unit_of_measure, x.reorder_level, 'Y');

MERGE INTO product p
USING (
  SELECT 'ACC-KEY-002' sku, 'USB Keyboard' product_name,
         'Demo full-size USB keyboard' description, 'Electronics' category_name,
         'Northstar Office & Tech' supplier_name, 'each' unit_of_measure, 6 reorder_level FROM dual
) x
ON (UPPER(p.sku) = UPPER(x.sku))
WHEN NOT MATCHED THEN INSERT
  (product_id, sku, product_name, description, category_id, supplier_id, unit_of_measure, reorder_level, active)
  VALUES (product_seq.NEXTVAL, x.sku, x.product_name, x.description,
          (SELECT category_id FROM category WHERE UPPER(category_name) = UPPER(x.category_name)),
          (SELECT supplier_id FROM supplier WHERE UPPER(supplier_name) = UPPER(x.supplier_name)),
          x.unit_of_measure, x.reorder_level, 'Y');

MERGE INTO product p
USING (
  SELECT 'OFF-PAP-A4' sku, 'A4 Copy Paper' product_name,
         'Demo 500-sheet paper ream' description, 'Office Supplies' category_name,
         'Northstar Office & Tech' supplier_name, 'ream' unit_of_measure, 10 reorder_level FROM dual
) x
ON (UPPER(p.sku) = UPPER(x.sku))
WHEN NOT MATCHED THEN INSERT
  (product_id, sku, product_name, description, category_id, supplier_id, unit_of_measure, reorder_level, active)
  VALUES (product_seq.NEXTVAL, x.sku, x.product_name, x.description,
          (SELECT category_id FROM category WHERE UPPER(category_name) = UPPER(x.category_name)),
          (SELECT supplier_id FROM supplier WHERE UPPER(supplier_name) = UPPER(x.supplier_name)),
          x.unit_of_measure, x.reorder_level, 'Y');

MERGE INTO product p
USING (
  SELECT 'PPE-GLV-M' sku, 'Work Gloves - Medium' product_name,
         'Demo reusable safety gloves, medium' description, 'Safety Equipment' category_name,
         'Harbor Safety Supply' supplier_name, 'pair' unit_of_measure, 12 reorder_level FROM dual
) x
ON (UPPER(p.sku) = UPPER(x.sku))
WHEN NOT MATCHED THEN INSERT
  (product_id, sku, product_name, description, category_id, supplier_id, unit_of_measure, reorder_level, active)
  VALUES (product_seq.NEXTVAL, x.sku, x.product_name, x.description,
          (SELECT category_id FROM category WHERE UPPER(category_name) = UPPER(x.category_name)),
          (SELECT supplier_id FROM supplier WHERE UPPER(supplier_name) = UPPER(x.supplier_name)),
          x.unit_of_measure, x.reorder_level, 'Y');

DECLARE
  v_admin_id app_user.user_id%TYPE;
BEGIN
  SELECT user_id INTO v_admin_id
  FROM app_user
  WHERE active = 'Y'
    AND role_id = (SELECT role_id FROM app_role WHERE role_name = 'Admin')
    AND ROWNUM = 1;

  INSERT INTO stock_transaction
    (transaction_id, product_id, quantity, transaction_type, occurred_at, user_id, reference, note)
  SELECT stock_transaction_seq.NEXTVAL, p.product_id, x.quantity, 'IN', SYSTIMESTAMP,
         v_admin_id, 'SAMPLE-OPENING-' || x.sku, 'Demonstration opening stock'
  FROM (
    SELECT 'ACC-MOU-001' sku, 24 quantity FROM dual UNION ALL
    SELECT 'ACC-KEY-002', 18 FROM dual UNION ALL
    SELECT 'OFF-PAP-A4', 40 FROM dual UNION ALL
    SELECT 'PPE-GLV-M', 30 FROM dual
  ) x
  JOIN product p ON UPPER(p.sku) = UPPER(x.sku)
  WHERE NOT EXISTS (
    SELECT 1 FROM stock_transaction t
    WHERE t.product_id = p.product_id
      AND t.transaction_type = 'IN'
      AND t.reference = 'SAMPLE-OPENING-' || x.sku
  );

  COMMIT;
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    ROLLBACK;
    RAISE_APPLICATION_ERROR(-20001, 'Create an active Admin account before loading sample stock.');
END;
/

PROMPT Sample inventory loaded (or it was already present).
