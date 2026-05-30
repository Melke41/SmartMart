import os
import sqlite3

def init_db():
    db_dir = os.path.dirname(os.path.abspath(__file__))
    db_path = os.path.join(db_dir, 'smartmart.db')
    schema_path = os.path.join(db_dir, 'schema.sql')
    seed_path = os.path.join(db_dir, 'seed.sql')

    print(f"Initializing database at: {db_path}")

    # If the file already exists, let's delete it so it's a fresh initialization
    if os.path.exists(db_path):
        print("Removing existing database file for a clean initialization...")
        os.remove(db_path)

    conn = sqlite3.connect(db_path)
    # Enable foreign keys
    conn.execute("PRAGMA foreign_keys = ON;")
    cursor = conn.cursor()

    # Read and execute schema
    print("Applying schema...")
    with open(schema_path, 'r', encoding='utf-8') as f:
        schema_sql = f.read()
    cursor.executescript(schema_sql)

    # Read and execute seed data
    print("Seeding database...")
    with open(seed_path, 'r', encoding='utf-8') as f:
        seed_sql = f.read()
    cursor.executescript(seed_sql)

    conn.commit()
    print("Database initialized successfully.")

    # 1. Print all table names
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';")
    tables = cursor.fetchall()
    print("\n--- Tables in Database ---")
    for t in tables:
        print(f"- {t[0]}")

    # 2. Print products with categories (JOIN) to verify
    print("\n--- Products & Categories JOIN Verification ---")
    cursor.execute("""
        SELECT p.product_name, c.category_name, p.price, p.stock_qty, s.name
        FROM products p
        JOIN categories c ON p.category_id = c.category_id
        JOIN suppliers s ON p.supplier_id = s.supplier_id
    """)
    products = cursor.fetchall()
    print(f"{'Product Name':<25} | {'Category':<15} | {'Price (ETB)':<10} | {'Stock':<6} | {'Supplier':<20}")
    print("-" * 82)
    for p in products:
        print(f"{p[0]:<25} | {p[1]:<15} | {p[2]:<10.2f} | {p[3]:<6} | {p[4]:<20}")

    conn.close()

if __name__ == '__main__':
    init_db()
