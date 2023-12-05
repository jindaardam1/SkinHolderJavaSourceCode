# Establecer las dimensiones de la consola
[console]::SetWindowSize(91, 63)

# Actualizar todo desde el repositorio de GitHub
Write-Host "[31mActualizando los registros desde el repositorio...[0m"
git pull | Out-Null
Write-Host "[32mRegistros actualizados.[0m"
Start-Sleep -Milliseconds 1000


# Poner la consola en UTF-8
chcp 65001 | Out-Null

# Ejecutar el JAR
java -jar ProyectoSteamAPI.jar

Clear-Host
Write-Host "[31mActualizando los registros con el repositorio...[0m"
# Añadir todos los nuevos registros al repositorio
git add * | Out-Null
# Hacer un commmit con los nuevos registros
git commit -m "Registros actualizados" | Out-Null
# Subir los cambios al repositorio de GitHub
git push | Out-Null
Write-Host "[32mRegistros actualizados.[0m"

Pause | Out-Null