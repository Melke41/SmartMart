-- USERS (passwords are SHA-256 hashed)
INSERT INTO users (username, password, role, full_name) VALUES
('admin',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',   'ADMIN',   'Solomon Haile'),
('manager', '866485796cfa8d7c0cf7111640205b83076433547577511d81f8030ae99ecea5', 'MANAGER', 'Tigist Bekele'),
('cashier1','b4c94003c562bb0d89535eca77f07284fe560fd48a7cc1ed99f0a56263d616ba', 'CASHIER', 'Dawit Alemu'),
('cashier2','cd821f965c2dbf9fd07ee140a65b0cb9d313fe9c6622fc3868d29d0824f8b9ff', 'CASHIER', 'Hanan Yusuf');

-- CATEGORIES
INSERT INTO categories (category_name) VALUES
('Beverages'), ('Dairy'), ('Snacks'), ('Household'), ('Personal Care');

-- SUPPLIERS
INSERT INTO suppliers (name, contact_phone, email, address) VALUES
('Almeta Trading',    '0911234567', 'almeta@gmail.com',   'Merkato, Addis Ababa'),
('Sunshine Imports',  '0922345678', 'sunshine@yahoo.com', 'Bole, Addis Ababa'),
('Atlas Distributors','0933456789', 'atlas@gmail.com',    'Piazza, Addis Ababa');

-- PRODUCTS
INSERT INTO products (product_name, category_id, supplier_id, price, stock_qty, low_stock_limit) VALUES
('Coca Cola 500ml',     1, 1, 25.00,  120, 20),
('Ambo Water 1L',       1, 1, 15.00,  200, 30),
('Lega Milk 1L',        2, 2, 55.00,   45, 15),
('Anbesa Biscuit 200g', 3, 2, 18.00,   80, 20),
('Choco Wafer',         3, 3, 12.00,   60, 15),
('Omo Detergent 500g',  4, 3, 75.00,   30, 10),
('Aqua Soap',           5, 1, 22.00,   90, 20),
('Nescafe 50g',         1, 2, 95.00,    8, 10),
('Excel Tissue',        4, 3, 35.00,   50, 15),
('Dove Shampoo 200ml',  5, 2, 145.00,  25, 10);

-- EMPLOYEES
INSERT INTO employees (user_id, full_name, phone, salary, hire_date) VALUES
(1, 'Solomon Haile', '0911111111', 15000.00, '2022-01-15'),
(2, 'Tigist Bekele', '0922222222', 12000.00, '2022-03-10'),
(3, 'Dawit Alemu',   '0933333333',  8000.00, '2023-06-01'),
(4, 'Hanan Yusuf',   '0944444444',  8000.00, '2023-08-20');
