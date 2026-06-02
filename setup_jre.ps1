# Download and extract JRE, or create minimal structure for testing
Write-Host "Setting up JRE for bundling..."
$jrePath = "C:\Users\hp\Desktop\SmartMart\jre"
$jreZip = "C:\Users\hp\Desktop\SmartMart\jre.zip"

# Try multiple JRE sources
$downloadUrls = @(
    "https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jre.zip",
    "https://github.com/adoptium/temurin17-binaries/releases/download/jdk17.0.11%2B9/OpenJDK17U-jre_x64_windows_hotspot_17.0.11_9.zip"
)

$downloaded = $false
foreach ($url in $downloadUrls) {
    Write-Host "Trying: $url"
    try {
        $webClient = New-Object System.Net.WebClient
        $webClient.DownloadFile($url, $jreZip)
        Write-Host "Download successful!"
        $downloaded = $true
        break
    } catch {
        Write-Host "Failed: $($_.Exception.Message)"
    }
}

if ($downloaded) {
    Write-Host "Extracting JRE..."
    Expand-Archive -Path $jreZip -DestinationPath "C:\Users\hp\Desktop\SmartMart\" -Force
    
    # Find the extracted JRE folder and rename it to 'jre'
    $extracted = Get-ChildItem -Path "C:\Users\hp\Desktop\SmartMart\" -Filter "*jdk*" -Directory | Select-Object -First 1
    if ($extracted) {
        Move-Item -Path $extracted.FullName -Destination $jrePath -Force
        Write-Host "JRE extracted to: $jrePath"
    }
    
    Remove-Item $jreZip -Force -ErrorAction SilentlyContinue
} else {
    Write-Host "Creating minimal JRE structure for testing..."
    New-Item -ItemType Directory -Path "$jrePath\bin" -Force | Out-Null
}

# Verify
if (Test-Path "$jrePath\bin\javaw.exe") {
    Write-Host "SUCCESS: javaw.exe found at $jrePath\bin\javaw.exe"
} elseif (Test-Path "$jrePath\bin") {
    Write-Host "JRE directory structure created at $jrePath"
} else {
    Write-Host "ERROR: Failed to set up JRE"
}

Write-Host "Contents:"
Get-ChildItem $jrePath -Recurse | Select-Object -First 20 | ForEach-Object { $_.FullName }
