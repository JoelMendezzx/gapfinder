# llenar_entities.ps1
# Ejecutar desde: D:\MOVILES\gapfinder\gapfinder\src\main\java\com\backend\gapfinder
# Uso: .\llenar_entities.ps1

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

foreach ($entidad in $entidades) {
    $carpetaLower = $entidad.ToLower()
    $rutaArchivo = Join-Path (Join-Path $basePath $carpetaLower) "$($entidad)Entity.java"

    if (-not (Test-Path $rutaArchivo)) {
        Write-Host "No existe (omitido): $rutaArchivo"
        continue
    }

    $contenido = @"
package com.backend.gapfinder.entities.$carpetaLower;

import com.backend.gapfinder.BaseEntity;

import jakarta.persistence.Entity;

@Entity

public class $($entidad)Entity extends BaseEntity {


}
"@

    # UTF8Encoding($false) escribe sin BOM. Con -Encoding UTF8, PowerShell 5.1
    # antepone EF BB BF y javac falla con: illegal character: '\ufeff'
    [System.IO.File]::WriteAllText($rutaArchivo, $contenido, (New-Object System.Text.UTF8Encoding $false))
    Write-Host "Actualizado: $rutaArchivo"
}

Write-Host "`nListo. Se actualizaron los archivos *Entity.java."
