# generar_entidades.ps1
# Ejecutar desde: D:\MOVILES\gapfinder\gapfinder\src\main\java\com\backend\gapfinder
# Uso: .\generar_entidades.ps1

$entidades = @(
    "User",
    "Interest",
    "Building",
    "Schedule",
    "VisibilitySettings",
    "DeviceToken",
    "Friendship",
    "Group",
    "ClassBlock",
    "Gap",
    "Match",
    "OpenTable",
    "Conversation",
    "Rating",
    "OpenTableParticipant",
    "Message",
    "Notification"
)

$basePath = Join-Path (Get-Location) "entities"

if (-not (Test-Path $basePath)) {
    New-Item -ItemType Directory -Path $basePath | Out-Null
}

foreach ($entidad in $entidades) {
    $carpeta = Join-Path $basePath $entidad.ToLower()

    if (-not (Test-Path $carpeta)) {
        New-Item -ItemType Directory -Path $carpeta | Out-Null
    }

    $archivos = @(
        "$($entidad)BasicDto.java",
        "$($entidad)CompleteDto.java",
        "$($entidad)Controller.java",
        "$($entidad)Service.java",
        "$($entidad)Entity.java",
        "$($entidad)Repository.java"
    )

    foreach ($archivo in $archivos) {
        $rutaArchivo = Join-Path $carpeta $archivo

        if (-not (Test-Path $rutaArchivo)) {
            New-Item -ItemType File -Path $rutaArchivo | Out-Null
            Write-Host "Creado: $rutaArchivo"
        } else {
            Write-Host "Ya existe (omitido): $rutaArchivo"
        }
    }
}

Write-Host "`nListo. Se generaron $($entidades.Count) carpetas dentro de 'entities/'."
