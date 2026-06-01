# 🚀 SmartMart Setup Guide — For GitHub Users

This guide explains how to set up and run SmartMart after cloning from GitHub.

---

## ⚡ **Quick Start (Easiest Method)**

### **Option 1: Using the Installer (Windows Only) — RECOMMENDED**

If you're on Windows and want the easiest installation:

1. **Download the installer:**
   - Go to GitHub Releases: https://github.com/Melke41/SmartMart/releases
   - Download **SmartMart_Setup.exe** (v1.0 or latest)

2. **Run the installer:**
   - Double-click `SmartMart_Setup.exe`
   - Follow the installation wizard
   - Accept all default settings
   - Click "Finish" to complete

3. **Launch SmartMart:**
   - Find "SmartMart" on your desktop or Start Menu
   - Double-click to launch
   - The database will initialize automatically on first run
   - Wait 10-15 seconds for the login screen

4. **Login:**
   ```
   Role: Admin        | Username: admin    | Password: admin123
   Role: Manager      | Username: manager  | Password: manager123
   Role: Cashier      | Username: cashier1 | Password: cashier123
   ```

✅ **Done!** No additional setup required.

---

### **Option 2: Using the Pre-Built JAR File**

If you want to run the application without installing (cross-platform):

#### **Prerequisites:**
- Java 8 or higher installed
  - **Check if installed:** Open Command Prompt and type `java -version`
  - **If not installed:** Download from https://java.com or https://adoptium.net/

#### **Steps:**

1. **Download the JAR:**
   - Go to GitHub Releases: https://github.com/Melke41/SmartMart/releases
   - Download **SmartMart.jar** (v1.0 or latest)

2. **Extract database files** (first time only):
   ```bash
   # If you have Python installed, run:
   python init_db.py
   
   # This creates the database file
   ```

3. **Run the application:**
   ```bash
   # Option A: Double-click SmartMart.jar
   # Option B: Command line
   java -jar SmartMart.jar
   ```

4. **Login** with credentials above

✅ **Done!**

---

### **Option 3: Building from Source (For Developers)**

If you want to compile SmartMart yourself or modify the code:

#### **Prerequisites:**
- Java Development Kit (JDK) 8 or higher
- An IDE like IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- Git (to clone the repository)

#### **Steps:**

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Melke41/SmartMart.git
   cd SmartMart
   ```

2. **Open project in IDE:**
   - IntelliJ IDEA: File → Open → select SmartMart folder
   - Eclipse: File → Import → Existing Projects into Workspace
   - VS Code: File → Open Folder

3. **Configure build path:**
   - Add all `.jar` files from the `lib/` folder to your classpath
   - Ensure source folder is `src/`
   - Ensure Java version is 8+

4. **Initialize database:**
   ```bash
   python database/init_db.py
   ```

5. **Run the application:**
   - Find `MainApp.java` in `src/smartmart/ui/`
   - Right-click → Run as Java Application
   - Or run from command line:
     ```bash
     javac -d bin -cp "src:lib/*" src/smartmart/ui/MainApp.java
     java -cp "bin:lib/*" smartmart.ui.MainApp
     ```

6. **Login** with credentials above

---

## 🛠️ **System Requirements**

| Component | Requirement |
|-----------|-------------|
| **Operating System** | Windows, macOS, or Linux |
| **Java** | Java 8 or higher (JRE or JDK) |
| **RAM** | 512 MB minimum (1 GB recommended) |
| **Disk Space** | 200 MB for installer, 50 MB for JAR + database |
| **Database** | SQLite (included, auto-initialized) |

---

## 📖 **Login Credentials (Demo Account)**

```
Admin Role:
  Username: admin
  Password: admin123
  Access: Full system access, product/employee/supplier management

Manager Role:
  Username: manager
  Password: manager123
  Access: Reports, sales analytics, inventory management

Cashier Role:
  Username: cashier1
  Password: cashier123
  Access: Point of Sale (POS), sales transactions, receipts
```

---

## ✅ **Verification Checklist**

After following setup steps above, verify:

- [ ] SmartMart launches without errors
- [ ] Login screen appears
- [ ] Can login with admin/admin123
- [ ] Dashboard loads (Admin Dashboard appears)
- [ ] No "Java not found" errors
- [ ] No "Database connection failed" errors
- [ ] Application is responsive (no freezing)

---

## 🐛 **Troubleshooting**

### **"Java is not installed or not recognized"**
- Install Java from https://java.com or https://adoptium.net/
- Restart your computer
- Try again

### **Database connection fails**
- Delete the existing `smartmart.db` file
- Run: `python database/init_db.py`
- Relaunch SmartMart

### **Installer fails on Windows**
- Run as Administrator: Right-click installer → Run as Administrator
- Disable antivirus temporarily (sometimes blocks new EXE files)
- Ensure you have write permissions to Program Files

### **JAR won't launch on double-click**
- Open Command Prompt and run: `java -jar SmartMart.jar`
- This will show any error messages
- Ensure Java 8+ is installed

### **Application is very slow**
- Ensure at least 1 GB RAM is available
- Close other applications
- Restart the application

---

## 📚 **Additional Resources**

- **GitHub Repository:** https://github.com/Melke41/SmartMart
- **Bug Reports:** GitHub Issues tab
- **Developer Documentation:** See [README.md](README.md) for architecture and OOP concepts
- **Course Info:** CoSc2051 OOP, Addis Ababa University, 2025

---

## 👥 **Development Team**

| Role | Name |
|------|------|
| Lead Developer | Melkamu Abyot |
| Backend Developer | Samuel Alemayehu |
| UI Developer | Mengistu Tark |

---

**Last Updated:** June 1, 2026  
**SmartMart Version:** 1.0 Final Release
