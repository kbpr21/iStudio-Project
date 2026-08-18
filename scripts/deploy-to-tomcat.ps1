# PowerShell Script: deploy-to-tomcat.ps1
# Automates Maven Build, Test, Package, and Tomcat Deployment on Windows/Local environments

$ErrorActionPreference = "Stop"

$AppName = "student-feedback-portal"
$WarFile = "target/$AppName.war"
$TomcatPort = "8081"
$TomcatWebapps = $env:TOMCAT_WEBAPPS
if (-not $TomcatWebapps) {
    $TomcatWebapps = "C:\apache-tomcat\webapps"
}
$HealthUrl = "http://localhost:$TomcatPort/$AppName/health"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " [CI/CD] Starting Maven Build & Tomcat Deployment" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Maven Compile & Test
Write-Host "[1/4] Running Maven clean test..." -ForegroundColor Yellow
mvn clean test

# 2. Maven Package
Write-Host "[2/4] Packaging WAR artifact..." -ForegroundColor Yellow
mvn package -DskipTests

if (-not (Test-Path $WarFile)) {
    Write-Error "ERROR: War file $WarFile was not generated!"
}

Write-Host "[3/4] Artifact generated at $WarFile" -ForegroundColor Green

# 3. Deploy if Tomcat directory exists
if (Test-Path $TomcatWebapps) {
    Write-Host "[4/4] Copying WAR to $TomcatWebapps..." -ForegroundColor Yellow
    Copy-Item $WarFile -Destination "$TomcatWebapps\$AppName.war" -Force
    Write-Host "WAR copied. Waiting 8 seconds for deployment..." -ForegroundColor Green
    Start-Sleep -Seconds 8

    try {
        $response = Invoke-RestMethod -Uri $HealthUrl -Method Get -TimeoutSec 10
        Write-Host "SUCCESS: Health check verified! Status: $($response.status)" -ForegroundColor Green
        Write-Host ($response | ConvertTo-Json -Depth 3)
    } catch {
        Write-Warning "Health check could not reach Tomcat on $HealthUrl. Ensure Tomcat is started on port $TomcatPort."
    }
} else {
    Write-Host "Note: Tomcat webapps directory ($TomcatWebapps) not found. To deploy, ensure Tomcat is installed or running via Docker." -ForegroundColor Gray
}
