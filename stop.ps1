# Oxford CSMS - Process Termination Helper
$pids = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
$stopped = $false

if ($pids) {
    foreach ($p in $pids) {
        if ($p -gt 0) {
            Write-Host "[INFO] Terminating Oxford CSMS process listening on port 8080 (PID: $p)..."
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

if ($stopped) {
    Write-Host "[INFO] Oxford CSMS application has been stopped successfully."
} else {
    Write-Host "[INFO] Oxford CSMS application is not running (Port 8080 is free)."
}
