<#
.SYNOPSIS
    Configures and initializes the Oxford International School CSMS Superuser Administrator.
.DESCRIPTION
    Runs the superuser administration configuration tool to set up username, password, email, and roles.
.EXAMPLE
    .\configure-superuser.ps1
.EXAMPLE
    .\configure-superuser.ps1 -Username admin -Password "SuperPass@2026"
#>
[CmdletBinding()]
param(
    [string]$Username,
    [string]$Password,
    [string]$Name,
    [string]$Email,
    [string]$Phone
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

$argsList = @()
if ($Username) { $argsList += "-u", $Username }
if ($Password) { $argsList += "-p", $Password }
if ($Name)     { $argsList += "-n", $Name }
if ($Email)    { $argsList += "-e", $Email }
if ($Phone)    { $argsList += "--phone", $Phone }

if (Get-Command python -ErrorAction SilentlyContinue) {
    & python "$scriptDir\configure_superuser.py" @argsList
} elseif (Get-Command py -ErrorAction SilentlyContinue) {
    & py "$scriptDir\configure_superuser.py" @argsList
} else {
    Write-Error "[ERROR] Python 3 was not found in PATH. Please ensure Python is installed."
    exit 1
}
