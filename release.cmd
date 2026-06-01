@echo off
cd C:\Users\hp\Desktop\SmartMart
"C:\Program Files\GitHub CLI\gh.exe" release create v1.0-release "release/SmartMart_Setup.exe" "release/SmartMart.jar" --title "SmartMart v1.0 — Final Release" --notes "## 🛒 SmartMart v1.0

**Retail Store Management System**
Developed for CoSc2051 OOP — Addis Ababa University 2025

---

### ⬇️ Downloads
| File | Description |
|------|-------------|
| SmartMart_Setup.exe | Windows installer — recommended for most users |
| SmartMart.jar | Runnable JAR — for users with Java already installed |

---

### 🚀 How to Install (Recommended)
1. Download SmartMart_Setup.exe
2. Run the installer and follow the wizard
3. SmartMart shortcut appears on your Desktop automatically
4. Double-click to launch

### 🔧 How to Run JAR (Alternative)
1. Make sure Java 8+ is installed — https://java.com
2. Download SmartMart.jar and database folder
3. Run: python database/init_db.py (first time only)
4. Run: java -jar SmartMart.jar

---

### 🔑 Login Credentials
| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Manager | manager | manager123 |
| Cashier | cashier1 | cashier123 |

---

### 👥 Development Team
- Melkamu Abyot — Lead Developer
- Samuel Alemayehu — Backend Developer
- Mengistu Tark — UI Developer

---

### 📚 Course Info
Object Oriented Programming (CoSc2051)
Addis Ababa University, 2025"
