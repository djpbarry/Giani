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
rem Call the wrapper by full path: a bare `mvnw.cmd` is not found when
rem NoDefaultCurrentDirectoryInExePath is set (as in some tool shells).
call "%CD%\mvnw.cmd" -q -DskipTests clean package
if errorlevel 1 (
    popd
    echo [install-to-fiji] ERROR: build failed.
    exit /b 1
)
popd

rem Remove any previously installed GIANI, CALM or IAClassLibrary jars from
rem BOTH plugins/ and jars/ (avoid duplicate or clashing net.calm classes).
rem Manual installs have put IAClassLibrary in plugins/, and CALM-era installs
rem left calm-*.jar / iaclasslibrary-*.jar. Matching is case-insensitive.
rem The prefixes are listed without wildcards on purpose: a wildcard item in a
rem FOR set is expanded against the *current* directory, so it would never match.
for %%d in ("%PLUGINS%" "%JARS%") do (
    for %%p in (giani calm iaclasslibrary) do (
        for %%f in ("%%~d\%%p-*.jar") do (
            del /q "%%f"
            echo [install-to-fiji] Removed old %%~nxf from %%~nxd
        )
    )
)

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

rem Install the IAClassLibrary dependency into jars/ (old copies removed above).
for %%f in ("%~dp0..\target\IAClassLibrary-*.jar") do (
    copy /y "%%f" "%JARS%\" >nul
    echo [install-to-fiji] Installed library %%~nxf
)

echo [install-to-fiji] Done. Restart Fiji to pick up the new plugin.
endlocal
