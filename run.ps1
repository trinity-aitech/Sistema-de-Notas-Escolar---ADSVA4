# Script para iniciar o servidor Java Web
Write-Host "Iniciando Sistema de Notas Escolar em Java Puro..." -ForegroundColor Green

if (-not (Test-Path "bin\br\com\escola\Main.class")) {
    Write-Host "Binários não encontrados. Executando build.ps1 primeiro..." -ForegroundColor Yellow
    .\build.ps1
}

java -cp "bin;lib\postgresql-42.7.3.jar" br.com.escola.Main
