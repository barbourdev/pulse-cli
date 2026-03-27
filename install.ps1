Write-Host "Building pulse..." -ForegroundColor Cyan
mvn package -q -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "[FAIL] Build falhou." -ForegroundColor Red
    exit 1
}

$pulseDir = "$env:USERPROFILE\.pulse"
if (!(Test-Path $pulseDir)) {
    New-Item -ItemType Directory -Path $pulseDir | Out-Null
}

Copy-Item "target\pulse.jar" "$pulseDir\pulse.jar" -Force

$wrapperContent = @'
@echo off
java -jar "%USERPROFILE%\.pulse\pulse.jar" %*
'@

Set-Content -Path "$pulseDir\pulse.cmd" -Value $wrapperContent

# Adicionar ao PATH do usuario se ainda nao estiver
$userPath = [System.Environment]::GetEnvironmentVariable("Path", "User")
if ($userPath -notlike "*$pulseDir*") {
    [System.Environment]::SetEnvironmentVariable("Path", "$userPath;$pulseDir", "User")
    Write-Host ""
    Write-Host "[ OK ] pulse instalado com sucesso!" -ForegroundColor Green
    Write-Host "  Feche e reabra o terminal, depois rode: pulse check --help"
} else {
    Write-Host ""
    Write-Host "[ OK ] pulse atualizado com sucesso!" -ForegroundColor Green
    Write-Host "  Rode: pulse check --help"
}
