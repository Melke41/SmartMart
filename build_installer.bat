@echo off
REM Build script for SmartMart with bundled JRE installer
REM This script compiles the installer using Inno Setup

cd /d "%~dp0"

echo.
echo ===================================
echo SmartMart Installer Build Script
echo ===================================
echo.

REM Check if iscc is available
where iscc >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] Inno Setup Compiler (iscc) not found in PATH
    echo.
    echo To build the installer, you need to:
    echo 1. Install Inno Setup from: https://www.innosetup.com/
    echo 2. Add Inno Setup to your system PATH, OR
    echo 3. Run this from: "C:\Program Files (x86)\Inno Setup 6\iscc.exe SmartMart_Installer.iss"
    echo.
    pause
    exit /b 1
)

echo [1/3] Verifying JRE structure...
if not exist "jre\bin\javaw.exe" (
    echo [ERROR] JRE not found at jre\bin\javaw.exe
    echo Please ensure JRE is extracted to the jre\ folder
    pause
    exit /b 1
)
echo [OK] JRE found

echo.
echo [2/3] Verifying SmartMart.jar...
if not exist "SmartMart.jar" (
    echo [ERROR] SmartMart.jar not found
    pause
    exit /b 1
)
echo [OK] SmartMart.jar found

echo.
echo [3/3] Compiling installer...
echo This may take a minute...
iscc SmartMart_Installer.iss

if %errorlevel% equ 0 (
    echo.
    echo ===================================
    echo [SUCCESS] Installer compiled!
    echo Output: release\SmartMart_Setup.exe
    echo ===================================
    echo.
    REM Show file size
    for %%F in (release\SmartMart_Setup.exe) do (
        echo File size: %%~zF bytes (%%~zF MB / 1048576)
    )
    echo.
) else (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

pause
