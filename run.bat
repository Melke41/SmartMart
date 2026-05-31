@echo off
cd /d "%~dp0"

IF NOT EXIST "database\smartmart.db" (
    echo Initializing database...
    python database\init_db.py > nul 2>&1
)

set NEEDS_COMPILE=0
IF NOT EXIST "out\smartmart\ui\MainApp.class" set NEEDS_COMPILE=1

IF %NEEDS_COMPILE%==1 (
    echo Compiling first run...
    javac -cp "lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-nop-1.7.36.jar" -d out src/smartmart/model/*.java src/smartmart/exception/*.java src/smartmart/util/*.java src/smartmart/dao/*.java src/smartmart/service/*.java src/smartmart/ui/*.java src/smartmart/ui/admin/*.java src/smartmart/ui/manager/*.java src/smartmart/ui/cashier/*.java
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed. Check errors above.
        pause
        exit /b %ERRORLEVEL%
    )
    xcopy /E /I /Y src\smartmart\ui\resources out\smartmart\ui\resources > nul 2>&1
)

start "" /b java -cp "out;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-nop-1.7.36.jar" smartmart.ui.MainApp
