@echo off
echo ================================
echo     SmartMart - Starting Up
echo ================================
cd /d C:\Users\hp\Desktop\SmartMart
if not exist out\smartmart\ui\MainApp.class (
    echo Compiling SmartMart...
    javac --release 17 -cp "lib/sqlite-jdbc-3.45.1.0.jar" -d out src/smartmart/model/*.java src/smartmart/exception/*.java src/smartmart/util/*.java src/smartmart/dao/*.java src/smartmart/service/*.java src/smartmart/ui/*.java src/smartmart/ui/admin/*.java src/smartmart/ui/manager/*.java src/smartmart/ui/cashier/*.java
    if %errorlevel% neq 0 (
        echo BUILD FAILED. Check errors above.
        pause
        exit /b 1
    )
)
echo Launching SmartMart...
java -cp "out;lib/sqlite-jdbc-3.45.1.0.jar" smartmart.ui.MainApp
