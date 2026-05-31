# SmartMart — Presentation Script

## Opening (30 seconds)
"Good [morning/afternoon]. Our project is SmartMart — a Retail Store Management System.
It manages the complete operations of a retail store: inventory, sales, employees, suppliers, and reporting.
It has three user roles: Admin, Manager, and Cashier — each with a completely different interface and permissions."

## Scene 1 — Login Screen (1 minute)
Point out:
- Professional branding on the left panel
- Feature highlights visible without logging in
- Role-based routing — same login screen sends each user to their own world
Demo: Type wrong password → show error. Then login as Admin.

## Scene 2 — Admin Panel (3 minutes)
1. Overview: "These four cards show live stats — products, revenue, employees, alerts"
2. Products: "Notice Nescafe is highlighted yellow — it's below the low stock threshold"
   - Add new product live
   - Edit a price live
3. Employees: "Each employee is linked to a user account with role-based access"
   - Add new employee live
4. Restock Orders: "When stock is low we raise a restock order to the supplier"
   - Create order, then mark received
   - "Notice the product stock just updated automatically"
5. Logout

## Scene 3 — Cashier POS (3 minutes)
1. "This is the Point of Sale screen — the most used screen in the system"
2. Search product, add to cart — "Notice the cart total updates instantly"
3. Add 3 different products
4. Point out tax calculation: "15% VAT calculated automatically"
5. Complete Sale — receipt dialog opens
6. "The receipt is formatted professionally and can be saved to file"
7. Transaction History: "Every cashier can review their own sales history"
8. Logout

## Scene 4 — Manager Dashboard (2 minutes)
1. Overview: "Revenue, transactions, alerts — all live data"
2. Sales Report: "Filter by date range, click any sale to drill into items"
3. EOD Report: "One click generates the complete end of day summary"
   - Generate live, point out sections
   - Save to file
4. "The manager has full visibility but cannot modify products or employees — that's access control"
5. Show About dialog — team members

## OOP Highlight (1 minute)
"Before we close, let me show you the OOP architecture:
- We have an abstract User class with three concrete subclasses — Admin, Manager, Cashier
- When the user logs in, polymorphism determines which dashboard opens automatically
- Every database operation throws a custom exception — OutOfStockException, UnauthorizedAccessException — which are caught and displayed to the user
- The Report module uses an abstract base class with three concrete report types
- The entire UI is built with Java Swing using all layout managers taught in class"

## Closing (30 seconds)
"SmartMart demonstrates every topic from our OOP course in a real, working application.
The full source code is on GitHub at [your repo URL].
Thank you — we are happy to take questions."

---

## Questions You Might Get — Prepared Answers

Q: Why did you choose SQLite over MySQL?
A: SQLite requires zero server setup — the entire database is a single file. Perfect for a desktop application that needs to run on any machine without configuration.

Q: Where is polymorphism used?
A: When a user logs in, the system calls currentUser.getDashboardTitle() and getDashboardPanel() — the correct implementation runs automatically based on whether the object is an Admin, Manager, or Cashier. We never check the role manually.

Q: What happens if a cashier tries to access the admin panel?
A: AuthService.requireRole() throws an UnauthorizedAccessException with the username and the attempted action. It's caught and shown to the user.

Q: Where is the abstract class?
A: The User class is abstract — you cannot instantiate it directly. And the Report class inside ReportService is abstract with an abstract generate() method implemented differently by SalesReport, InventoryReport, and EODReport.

Q: Where is method overloading?
A: Product.isLowStock() with no arguments checks against the stored threshold. isLowStock(int customLimit) checks against any custom value you pass in.

Q: How is the database connected?
A: Through a singleton DatabaseConnection class using JDBC. The singleton ensures only one connection is open at a time. All queries use PreparedStatement to prevent SQL injection.
