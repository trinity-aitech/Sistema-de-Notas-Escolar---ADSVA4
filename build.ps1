# Script de compilação em Java puro (sem necessidade de Maven ou Gradle instalados)
Write-Host "Compilando backend Java..." -ForegroundColor Cyan

if (-not (Test-Path "lib\postgresql-42.7.3.jar")) {
    Write-Host "Driver PostgreSQL não encontrado em lib/. Baixando automaticamente..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path "lib" | Out-Null
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar" -OutFile "lib\postgresql-42.7.3.jar"
}

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$javaFiles = Get-ChildItem -Recurse -Filter "*.java" src\main\java | ForEach-Object { $_.FullName }
$testFiles = Get-ChildItem -Recurse -Filter "*.java" src\test\java | ForEach-Object { $_.FullName }

javac -encoding UTF-8 -cp "lib\postgresql-42.7.3.jar" -d bin $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backend compilado com sucesso em bin/" -ForegroundColor Green
    
    # Compila e roda os testes
    javac -encoding UTF-8 -cp "bin;lib\postgresql-42.7.3.jar" -d bin $testFiles
    Write-Host "Executando testes unitários do Motor de Regras LDB/CEE..." -ForegroundColor Cyan
    java -cp "bin" br.com.escola.modulos.regras.MotorRegrasTest
} else {
    Write-Host "❌ Falha na compilação do Java" -ForegroundColor Red
}
