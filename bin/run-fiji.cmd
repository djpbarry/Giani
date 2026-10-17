@echo off
rem Launch Fiji so the GIANI plugin can be used interactively.
rem
rem Usage:   run-fiji.cmd [path\to\Fiji]
rem Default: FIJI_DIR from bin\local-env.cmd (gitignored); see local-env.cmd.example
rem
rem This Fiji is a nojava build (no bundled JRE). The jaunch launcher finds Java
rem 21 via JAVA_HOME. Launching via the bundled launcher gives the full
rem Fiji/ImageJ2 environment.

setlocal

rem Load machine-specific paths (FIJI_DIR, JAVA_HOME) if present.
call "%~dp0local-env.cmd" 2>nul

rem A command-line argument overrides the env file.
if not "%~1"=="" set "FIJI_DIR=%~1"

if "%FIJI_DIR%"=="" (
    echo [run-fiji] ERROR: FIJI_DIR not set. Create bin\local-env.cmd or pass a path.
    exit /b 1
)

set "LAUNCHER=%FIJI_DIR%\fiji-windows-x64.exe"
if not exist "%LAUNCHER%" (
    echo [run-fiji] ERROR: launcher not found: "%LAUNCHER%"
    exit /b 1
)

pushd "%FIJI_DIR%"
start "Fiji" "%LAUNCHER%"
popd

endlocal
