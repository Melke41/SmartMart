@echo off
cd /d "%~dp0"

echo Compiling...
javac -cp "lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-nop-1.7.36.jar" -d out src/smartmart/model/*.java src/smartmart/exception/*.java src/smartmart/util/*.java src/smartmart/dao/*.java src/smartmart/service/*.java src/smartmart/ui/*.java src/smartmart/ui/admin/*.java src/smartmart/ui/manager/*.java src/smartmart/ui/cashier/*.java
if %ERRORLEVEL% NEQ 0 (
    echo Build failed. Check errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo Launching SmartMart...
java -cp "out;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-nop-1.7.36.jar" smartmart.ui.MainApp
