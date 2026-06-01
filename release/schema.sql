-- 1. ROLES & USERS
CREATE TABLE IF NOT EXISTS users (
    user_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    username    TEXT NOT NULL UNIQUE,
    password    TEXT NOT NULL,
    role        TEXT NOT NULL CHECK(role IN ('ADMIN','MANAGER','CASHIER')),
    full_name   TEXT NOT NULL,
    is_active   INTEGER NOT NULL DEFAULT 1,
    created_at  TEXT NOT NULL DEFAULT (datetime('now'))
);

-- 2. CATEGORIES
CREATE TABLE IF NOT EXISTS categories (
    category_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL UNIQUE
);

-- 3. SUPPLIERS
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT NOT NULL,
    contact_phone TEXT,
    email         TEXT,
    address       TEXT,
    created_at    TEXT NOT NULL DEFAULT (datetime('now'))
);

-- 4. PRODUCTS
CREATE TABLE IF NOT EXISTS products (
    product_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    product_name    TEXT NOT NULL,
    category_id     INTEGER NOT NULL,
    supplier_id     INTEGER NOT NULL,
    price           REAL NOT NULL CHECK(price >= 0),
    stock_qty       INTEGER NOT NULL DEFAULT 0 CHECK(stock_qty >= 0),
    low_stock_limit INTEGER NOT NULL DEFAULT 10,
    created_at      TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- 5. EMPLOYEES
CREATE TABLE IF NOT EXISTS employees (
    employee_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER NOT NULL UNIQUE,
    full_name     TEXT NOT NULL,
    phone         TEXT,
    salary        REAL NOT NULL DEFAULT 0,
    hire_date     TEXT NOT NULL DEFAULT (date('now')),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 6. SALES
CREATE TABLE IF NOT EXISTS sales (
    sale_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    cashier_id    INTEGER NOT NULL,
    total_amount  REAL NOT NULL DEFAULT 0,
    sale_date     TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (cashier_id) REFERENCES users(user_id)
);

-- 7. SALE ITEMS
CREATE TABLE IF NOT EXISTS sale_items (
    item_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    sale_id       INTEGER NOT NULL,
    product_id    INTEGER NOT NULL,
    quantity      INTEGER NOT NULL CHECK(quantity > 0),
    unit_price    REAL NOT NULL,
    subtotal      REAL NOT NULL,
    FOREIGN KEY (sale_id)    REFERENCES sales(sale_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 8. RESTOCK ORDERS
CREATE TABLE IF NOT EXISTS restock_orders (
    order_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id    INTEGER NOT NULL,
    supplier_id   INTEGER NOT NULL,
    quantity      INTEGER NOT NULL CHECK(quantity > 0),
    status        TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','RECEIVED','CANCELLED')),
    order_date    TEXT NOT NULL DEFAULT (datetime('now')),
    received_date TEXT,
    FOREIGN KEY (product_id)  REFERENCES products(product_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- 9. LOW STOCK ALERTS LOG
CREATE TABLE IF NOT EXISTS alerts (
    alert_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id  INTEGER NOT NULL,
    message     TEXT NOT NULL,
    is_resolved INTEGER NOT NULL DEFAULT 0,
    created_at  TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
