# Download Eclipse Temurin JRE 17 for Windows x64
$apiUrl = "https://api.adoptium.net/v3/latest/17/jre?os=windows&arch=x64&image_type=jre&release_type=ga&jvm_impl=hotspot"
$outputPath = "C:\Users\hp\Desktop\SmartMart\jre.zip"

Write-Host "Fetching JRE download link from Adoptium API..."
try {
    $response = Invoke-RestMethod -Uri $apiUrl -ErrorAction Stop
    if ($response.binaries -and $response.binaries[0].package.link) {
        $downloadUrl = $response.binaries[0].package.link
        $fileName = $response.binaries[0].package.name
        Write-Host "Found: $fileName"
        Write-Host "Downloading from: $downloadUrl"
        
        # Download the file
        $webClient = New-Object System.Net.WebClient
        $webClient.DownloadFile($downloadUrl, $outputPath)
        
        # Verify download
        if (Test-Path $outputPath) {
            $size = (Get-Item $outputPath).Length / 1MB
            Write-Host "Download complete! File size: $([math]::Round($size, 2)) MB"
        }
    } else {
        Write-Host "API response invalid"
    }
} catch {
    Write-Host "Error: $_"
}
