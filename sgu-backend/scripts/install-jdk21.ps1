<#
PowerShell script para descargar e instalar Eclipse Temurin (JDK 21) y configurar JAVA_HOME y PATH en Windows.
Uso:
- Abrir PowerShell como Administrador
- Ejecutar: .\scripts\install-jdk21.ps1

Advertencia: Este script realiza una instalación silenciosa del JDK y modifica variables de entorno del sistema. Ejecutar sólo si confías en la fuente.
#>

function Ensure-RunAsAdmin {
    $current = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($current)
    if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
        Write-Host "El script necesita privilegios de administrador. Reiniciando como admin..." -ForegroundColor Yellow
        Start-Process powershell -Verb RunAs -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`""
        exit
    }
}

Ensure-RunAsAdmin

Write-Host "Comprobando versión de javac existente..." -ForegroundColor Cyan
$javacVersion = & javac -version 2>&1 | Out-String
if ($javacVersion -match "21\.") {
    Write-Host "Parece que ya tienes Java 21 instalado:" -ForegroundColor Green
    Write-Host $javacVersion
    exit 0
}

# URL de la API para obtener la última build de Temurin 21 (adoptium API). Esta URL suele redirigir al binario.
$apiUrl = "https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal"
$tempFile = Join-Path $env:TEMP "temurin-jdk-21.msi"

Write-Host "Descargando JDK 21 desde Adoptium..." -ForegroundColor Cyan
try {
    Invoke-WebRequest -Uri $apiUrl -OutFile $tempFile -UseBasicParsing -ErrorAction Stop
} catch {
    Write-Error "Error descargando JDK: $_. Exception.Message"
    exit 1
}

if (-not (Test-Path $tempFile)) {
    Write-Error "No se descargó el instalador. Abortando."
    exit 1
}

Write-Host "Instalando JDK 21 (silencioso)... esto puede tardar unos minutos" -ForegroundColor Cyan
$msiArgs = "/i `"$tempFile`" /qn /norestart"
$proc = Start-Process -FilePath msiexec.exe -ArgumentList $msiArgs -Wait -PassThru
if ($proc.ExitCode -ne 0) {
    Write-Error "msiexec devolvió código $($proc.ExitCode). Revisa el instalador o ejecuta manualmente: msiexec /i \"$tempFile\""
    exit 1
}

# Intentar detectar ruta de instalación (común en: C:\Program Files\Eclipse Adoptium\jdk-21*)
$possiblePatterns = @("C:\\Program Files\\Eclipse Adoptium\\jdk-21*", "C:\\Program Files\\Temurin\\jdk-21*", "C:\\Program Files\\Java\\jdk-21*", "C:\\Program Files\\Adoptium\\jdk-21*")
$installPath = $null
foreach ($p in $possiblePatterns) {
    $found = Get-ChildItem -Path $p -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) { $installPath = $found.FullName; break }
}

# Si no se detecta, buscar carpetas que contengan 'jdk-21' en Program Files (mayor trabajo, silencioso)
if (-not $installPath) {
    $found = Get-ChildItem -Path "C:\\Program Files" -Recurse -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -match "jdk-21" } | Select-Object -First 1
    if ($found) { $installPath = $found.FullName }
}

if (-not $installPath) {
    Write-Warning "No pude detectar automáticamente la ruta de instalación del JDK. Puedes indicar la ruta manualmente."
    Write-Host "El instalador MSI se descargó en: $tempFile" -ForegroundColor Yellow
    Write-Host "Si instalaste JDK manualmente, establece JAVA_HOME al directorio raíz del JDK (ej: C:\\Program Files\\Eclipse Adoptium\\jdk-21.x.x)" -ForegroundColor Yellow
    exit 0
}

Write-Host "JDK instalado en: $installPath" -ForegroundColor Green

# Configurar variables de entorno a nivel máquina
Write-Host "Configurando JAVA_HOME y PATH (Machine)..." -ForegroundColor Cyan
try {
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $installPath, [EnvironmentVariableTarget]::Machine)
    $machinePath = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::Machine)
    $binPath = Join-Path $installPath "bin"
    if ($machinePath -notlike "*${binPath}*") {
        $newPath = "${binPath};${machinePath}"
        [Environment]::SetEnvironmentVariable("Path", $newPath, [EnvironmentVariableTarget]::Machine)
    }
} catch {
    Write-Error "No se pudo actualizar variables de entorno: $_"
    exit 1
}

Write-Host "Instalación y configuración completadas. Debes cerrar y abrir la terminal (cmd/powershell/IDE) para que los cambios surtan efecto." -ForegroundColor Green
Write-Host "Verifica en una nueva terminal con: java -version ; javac -version ; echo %JAVA_HOME%" -ForegroundColor Cyan

# Limpiar archivo temporal (opcional)
try { Remove-Item -Path $tempFile -Force -ErrorAction SilentlyContinue } catch {}

exit 0

