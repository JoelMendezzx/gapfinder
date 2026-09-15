# llenar_repositories.ps1
# Ejecutar desde: D:\MOVILES\gapfinder\gapfinder\src\main\java\com\backend\gapfinder
# Uso: .\llenar_repositories.ps1

$entidades = @(
    "User",
    "Interest",
    "Building",
    "ClassBlock",
    "Friendship",
    "Group",
    "VisibilitySettings",
    "Gap",
    "UserLocationLog",
    "Match",
    "OpenTable",
    "Message",
    "Rating",
    "OpenTableParticipant",
    "Notification"
)

$basePath = Join-Path (Get-Location) "entities"

foreach ($entidad in $entidades) {
    $carpetaLower = $entidad.ToLower()
    $carpeta = Join-Path $basePath $carpetaLower

    if (-not (Test-Path $carpeta)) {
        New-Item -ItemType Directory -Path $carpeta | Out-Null
        Write-Host "Carpeta creada: $carpeta"
    }

    $rutaArchivo = Join-Path $carpeta "$($entidad)Repository.java"

    $contenido = @"
package com.backend.gapfinder.entities.$carpetaLower;

import org.springframework.data.jpa.repository.JpaRepository;

public interface $($entidad)Repository extends JpaRepository<$($entidad)Entity, Long> {

}
"@

    Set-Content -Path $rutaArchivo -Value $contenido -Encoding UTF8
    Write-Host "Actualizado: $rutaArchivo"
}

Write-Host "`nListo. Se generaron los archivos *Repository.java."
