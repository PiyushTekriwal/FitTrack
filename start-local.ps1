$RootPath = if (Test-Path "X:\backend") { "X:" } else { $PSScriptRoot }

Write-Host "Starting Eureka Server..."
Start-Process "java.exe" -ArgumentList "-Dspring.profiles.active=local", "-jar", "$RootPath\backend\eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar"
Start-Sleep -Seconds 12

Write-Host "Starting Config Server..."
Start-Process "java.exe" -ArgumentList "-Dspring.profiles.active=local", "-jar", "$RootPath\backend\config-server\target\config-server-0.0.1-SNAPSHOT.jar"
Start-Sleep -Seconds 8

Write-Host "Starting Domain Services..."
Start-Process "java.exe" -ArgumentList "-Dspring.profiles.active=local", "-jar", "$RootPath\backend\user-service\target\user-service-0.0.1-SNAPSHOT.jar"
Start-Process "java.exe" -ArgumentList "-Dspring.profiles.active=local", "-jar", "$RootPath\backend\activity-service\target\activity-service-0.0.1-SNAPSHOT.jar"
Start-Process "java.exe" -ArgumentList "-Dspring.profiles.active=local", "-jar", "$RootPath\backend\recommendation-service\target\recommendation-service-0.0.1-SNAPSHOT.jar"
Start-Process "java.exe" -ArgumentList "-Dspring.profiles.active=local", "-jar", "$RootPath\backend\plan-service\target\plan-service-0.0.1-SNAPSHOT.jar"

Write-Host "Starting API Gateway..."
Start-Process "java.exe" -ArgumentList "-Dspring.profiles.active=local", "-jar", "$RootPath\backend\api-gateway\target\api-gateway-0.0.1-SNAPSHOT.jar"
Start-Sleep -Seconds 5

Write-Host "Starting Frontend..."
Start-Process "cmd.exe" -ArgumentList "/c", "npm.cmd", "run", "dev" -WorkingDirectory "$RootPath\frontend"

Write-Host "All services started! URLs:"
Write-Host "Frontend: http://localhost:5173"
Write-Host "API Gateway: http://localhost:8080"
Write-Host "Eureka: http://localhost:8761"
