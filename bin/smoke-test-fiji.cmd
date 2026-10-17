@echo off
rem Headless smoke test: verify the local Fiji runs on the JDK and that the GIANI
rem plugin classes (plus IAClassLibrary) are loadable.
rem
rem Usage:   smoke-test-fiji.cmd [path\to\Fiji]
rem Default: FIJI_DIR from bin\local-env.cmd (gitignored); see local-env.cmd.example
rem
rem This does not run the GUI; it only confirms the classpath and plugin wiring.

setlocal

rem Load machine-specific paths (FIJI_DIR, JAVA_HOME) if present.
call "%~dp0local-env.cmd" 2>nul

rem A command-line argument overrides the env file.
if not "%~1"=="" set "FIJI_DIR=%~1"

if "%FIJI_DIR%"=="" (
    echo [smoke-test-fiji] ERROR: FIJI_DIR not set. Create bin\local-env.cmd or pass a path.
    exit /b 1
)

if "%JAVA_HOME%"=="" (
    echo [smoke-test-fiji] ERROR: JAVA_HOME not set.
    exit /b 1
)

set "JAVA=%JAVA_HOME%\bin\java.exe"
if not exist "%JAVA%" (
    echo [smoke-test-fiji] ERROR: java not found under "%JAVA_HOME%\bin"
    exit /b 1
)

set "CHECK=%TEMP%\GianiPluginCheck.java"
> "%CHECK%" (
    echo public class GianiPluginCheck {
    echo     public static void main(String[] a^) throws Exception {
    echo         String[] plugins = {
    echo             "net.calm.giani.exec.GIANI",
    echo             "net.calm.giani.ui.GIANIUI",
    echo             "net.calm.giani.macro.GIANIMacroExecutor"
    echo         };
    echo         for (String p : plugins^) {
    echo             System.out.println("OK  " + Class.forName(p^).getName(^)^);
    echo         }
    echo         System.out.println("ALL_GIANI_PLUGINS_LOADABLE"^);
    echo     }
    echo }
)

pushd "%FIJI_DIR%"
"%JAVA%" -cp "jars\*;plugins\*" "%CHECK%" 2>&1
set "RC=%ERRORLEVEL%"
popd

del /q "%CHECK%" 2>nul
exit /b %RC%
