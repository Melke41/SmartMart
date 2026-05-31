$WshShell = New-Object -comObject WScript.Shell
$DesktopPath = [Environment]::GetFolderPath("Desktop")
$Shortcut = $WshShell.CreateShortcut("$DesktopPath\SmartMart.lnk")
$Shortcut.TargetPath = "cmd.exe"
$Shortcut.Arguments = '/c "cd /d C:\Users\hp\Desktop\SmartMart && run.bat"'
$Shortcut.WorkingDirectory = "C:\Users\hp\Desktop\SmartMart"
$Shortcut.IconLocation = "C:\Users\hp\Desktop\SmartMart\docs\smartmart.ico"
$Shortcut.WindowStyle = 7 # Minimized to avoid flashing a large console if possible
$Shortcut.Save()
Write-Host "Shortcut created at $DesktopPath\SmartMart.lnk"
