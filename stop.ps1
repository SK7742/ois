# Oxford CSMS - Process & Container Termination Helper
Write-Host "[INFO] Stopping Oxford CSMS Docker containers..."
docker compose down 2>$null

$pids = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
$stopped = $false

if ($pids) {
    foreach ($p in $pids) {
        if ($p -gt 0) {
            Write-Host "[INFO] Terminating local process listening on port 8080 (PID: $p)..."
            Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
            $stopped = $true
        }
    }
}

$backendProcs = Get-CimInstance Win32_Process -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*oxford-csms-backend*' }
if ($backendProcs) {
    foreach ($proc in $backendProcs) {
        Write-Host "[INFO] Terminating background Java process (PID: $($proc.ProcessId))..."
        Stop-Process -Id $proc.ProcessId -Force -ErrorAction SilentlyContinue
        $stopped = $true
    }
}

Write-Host "[INFO] Oxford CSMS application has been stopped successfully."
