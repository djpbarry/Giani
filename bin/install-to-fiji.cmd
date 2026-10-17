@echo off
rem Install the built GIANI plugin jar and its IAClassLibrary dependency into a
rem local Fiji.
rem
rem Usage:   install-to-fiji.cmd [path\to\Fiji]
rem Default: FIJI_DIR from bin\local-env.cmd (gitignored); see local-env.cmd.example
rem
rem The GIANI plugin jar goes into plugins/; IAClassLibrary goes into jars/.
rem mcib3d-core (the segmentation-result container) is expected to be provided
rem by Fiji's mcib3d-suite update site, and TrackMate/ImageJ by the base Fiji.

setlocal enabledelayedexpansion

rem Load machine-specific paths (FIJI_DIR, JAVA_HOME) if present.
call "%~dp0local-env.cmd" 2>nul

rem A command-line argument overrides the env file.
if not "%~1"=="" set "FIJI_DIR=%~1"

if "%FIJI_DIR%"=="" (
    echo [install-to-fiji] ERROR: FIJI_DIR not set. Create bin\local-env.cmd or pass a path.
    exit /b 1
)

set "PLUGINS=%FIJI_DIR%\plugins"
set "JARS=%FIJI_DIR%\jars"

if not exist "%PLUGINS%" (
    echo [install-to-fiji] ERROR: Fiji plugins directory not found: "%PLUGINS%"
    exit /b 1
)
if not exist "%JARS%" (
    echo [install-to-fiji] ERROR: Fiji jars directory not found: "%JARS%"
    exit /b 1
)

rem Build the plugin (skip tests). `clean` first so stale versioned jars from
rem previous builds aren't copied. Run from the repo root.
pushd "%~dp0.."
call mvnw.cmd -q -DskipTests clean package
if errorlevel 1 (
    popd
    echo [install-to-fiji] ERROR: build failed.
    exit /b 1
)
popd

rem Remove any previously installed GIANI plugin jar (avoid duplicate classes).
del /q "%PLUGINS%\giani-*.jar" 2>nul

rem Copy the freshly built plugin jar (skip -sources / -javadoc / -tests).
set "INSTALLED="
for %%f in ("%~dp0..\target\giani-*.jar") do (
    echo %%~nxf | findstr /i "sources javadoc tests" >nul
    if errorlevel 1 (
        copy /y "%%f" "%PLUGINS%\" >nul
        echo [install-to-fiji] Installed plugin %%~nxf
        set "INSTALLED=1"
    )
)

if not defined INSTALLED (
    echo [install-to-fiji] ERROR: no plugin jar found under target\giani-*.jar
    exit /b 1
)

rem Install the IAClassLibrary dependency into jars/ (replace any old version).
del /q "%JARS%\IAClassLibrary-*.jar" 2>nul
for %%f in ("%~dp0..\target\IAClassLibrary-*.jar") do (
    copy /y "%%f" "%JARS%\" >nul
    echo [install-to-fiji] Installed library %%~nxf
)

echo [install-to-fiji] Done. Restart Fiji to pick up the new plugin.
endlocal
