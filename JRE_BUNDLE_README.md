# SmartMart JRE Bundle Setup Guide

## Overview
SmartMart installer now bundles Java Runtime Environment (JRE) to eliminate the need for users to install Java separately.

## Changes Made

### 1. Updated SmartMart_Installer.iss
- **Added JRE to [Files] section**: Includes entire `jre/` folder with all subfolders
- **Updated [Icons] section**: Desktop and Start Menu shortcuts now use `javaw.exe` from bundled JRE
- **Updated [Run] section**: Installer startup command uses bundled JRE
- **Simplified [Code] section**: Removed Java prerequisite check since Java is bundled

### 2. JRE Directory Structure
```
SmartMart/
├── jre/
│   ├── bin/
│   │   ├── java.exe      (Java CLI)
│   │   └── javaw.exe     (Java GUI - no console window)
│   ├── conf/
│   ├── lib/
│   └── ...
├── SmartMart_Installer.iss    (Updated)
└── build_installer.bat        (Build script)
```

## Building the Installer

### Prerequisites
1. **Inno Setup** - Download from https://www.innosetup.com/
2. **JRE** - Extract to `jre/` folder (can be downloaded from):
   - Eclipse Adoptium: https://adoptium.net/
   - Amazon Corretto: https://aws.amazon.com/corretto/
   - Oracle: https://java.com/download/

### Download JRE
```powershell
# Option 1: Using PowerShell (auto-extract)
.\setup_jre.ps1

# Option 2: Manual download
# Download JRE 17 zip from adoptium.net
# Extract to: SmartMart/jre/
# Verify: jre/bin/javaw.exe exists
```

### Compile Installer
```cmd
# Method 1: Using build script (if Inno Setup in PATH)
build_installer.bat

# Method 2: Using Inno Setup GUI
# Open SmartMart_Installer.iss in Inno Setup IDE and click "Compile"

# Method 3: Manual iscc command
"C:\Program Files (x86)\Inno Setup 6\iscc.exe" SmartMart_Installer.iss
```

### Verify Build
```
Output directory: release/
File: SmartMart_Setup.exe
Expected size: ~150-200 MB (includes bundled JRE)
```

## Installation Experience (User Perspective)

### Before (Requires Java)
```
1. User downloads SmartMart_Setup.exe (~15 MB)
2. User runs installer
3. Error: "Java not installed"
4. User must:
   - Visit java.com
   - Download and install Java (~150 MB)
   - Restart
   - Run SmartMart installer again
```

### After (No Java Required)
```
1. User downloads SmartMart_Setup.exe (~150-200 MB)
2. User runs installer
3. Selects installation folder
4. Installation completes (includes Java)
5. Desktop shortcut launches SmartMart immediately
6. No external dependencies needed
```

## Testing New Installer

### Test on Clean Machine
1. Use VM or machine **without Java installed**
2. Download `release/SmartMart_Setup.exe`
3. Run installer
4. Verify:
   - No Java prerequisite error appears
   - Installation completes successfully
   - Desktop shortcut appears
   - Double-clicking shortcut opens SmartMart login
   - Database initializes on first launch

### Verify Java Bundling
```cmd
cd "C:\Program Files\SmartMart"
dir jre\bin\javaw.exe
# Should show: javaw.exe exists
```

## File Changes Summary

### Modified Files
- `SmartMart_Installer.iss` - Installer configuration

### New Files
- `jre/` - Directory with bundled JRE
- `build_installer.bat` - Build helper script
- `JRE_BUNDLE_README.md` - This documentation

### Commits
- `build: bundle JRE in installer — no Java prerequisite needed`

## GitHub Release Update
When ready to release, update v1.0-release with:
```
Files:
- SmartMart_Setup.exe (with bundled JRE)
- SmartMart.jar (standalone version)

Release notes should mention:
"SmartMart_Setup.exe now includes Java runtime — 
no installation required beyond the installer itself"
```

## Troubleshooting

### "iscc not found" error
- Install Inno Setup from https://www.innosetup.com/
- Or use full path: `"C:\Program Files (x86)\Inno Setup 6\iscc.exe" SmartMart_Installer.iss`

### JRE not bundled in installer
- Verify `jre/bin/javaw.exe` exists
- Check SmartMart_Installer.iss [Files] section includes JRE line
- Recompile installer

### Installer too large
- Normal! Installer includes entire JRE (~150 MB)
- After installation, total disk usage: ~300 MB
- Can reduce by using JRE instead of full JDK

### Shortcuts don't launch
- Verify `-jar ""{app}\SmartMart.jar""` parameters in [Icons] section
- Check jre\bin\javaw.exe was installed to program files
- Test from command line: `javaw.exe -jar SmartMart.jar`

## References
- Inno Setup: https://www.innosetup.com/
- Adoptium/Temurin: https://adoptium.net/
- Java parameters: https://docs.oracle.com/en/java/javase/17/docs/specs/man/java.html
