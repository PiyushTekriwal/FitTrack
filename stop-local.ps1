Write-Host "Stopping all local java and node processes..."
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Stop-Process -Name node -Force -ErrorAction SilentlyContinue
Write-Host "All processes stopped."
