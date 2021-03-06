@ECHO OFF

::----------------------------------------------------------------------
:: @@product_full@@ startup script.
::----------------------------------------------------------------------

:: ---------------------------------------------------------------------
:: Ensure IDE_HOME points to the directory where the IDE is installed.
:: ---------------------------------------------------------------------
SET IDE_BIN_DIR=%~dp0
SET IDE_HOME=%IDE_BIN_DIR%\..

:: ---------------------------------------------------------------------
:: Locate a JDK installation directory which will be used to run the IDE.
:: Try (in order): @@product_uc@@_JDK, @@vm_options@@.jdk, ..\jre, JDK_HOME, JAVA_HOME.
:: ---------------------------------------------------------------------
SET JDK=
IF EXIST "%@@product_uc@@_JDK%" (
  SET JDK=%@@product_uc@@_JDK%
)
IF "%JDK%" == "" (
  SET BITS=64
  SET USER_JDK64_FILE=%USERPROFILE%\.@@system_selector@@\config\@@vm_options@@.jdk
  SET BITS=
  SET USER_JDK_FILE=%USERPROFILE%\.@@system_selector@@\config\@@vm_options@@.jdk
  IF EXIST "%USER_JDK64_FILE%" (
    SET /P JDK=<%USER_JDK64_FILE%
    IF NOT EXIST "%JDK%" SET JDK=%IDE_HOME%\%JDK%
  ) ELSE (
    IF EXIST "%USER_JDK_FILE%" (
      SET /P JDK=<%USER_JDK_FILE%
      IF NOT EXIST "%JDK%" SET JDK=%IDE_HOME%\%JDK%
    )
  )
)
IF "%JDK%" == "" (
  IF EXIST "%IDE_HOME%\jre" SET JDK=%IDE_HOME%\jre
)
IF "%JDK%" == "" (
  IF EXIST "%JDK_HOME%" SET JDK=%JDK_HOME%
)
IF "%JDK%" == "" (
  IF EXIST "%JAVA_HOME%" SET JDK=%JAVA_HOME%
)

SET JAVA_EXE=%JDK%\bin\java.exe
IF NOT EXIST "%JAVA_EXE%" SET JAVA_EXE=%JDK%\jre\bin\java.exe
IF NOT EXIST "%JAVA_EXE%" (
  ECHO ERROR: cannot start IntelliJ IDEA.
  ECHO No JDK found. Please validate either IDEA_JDK, JDK_HOME or JAVA_HOME points to valid JDK installation.
  ECHO
  EXIT /B
)

SET JRE=%JDK%
IF EXIST "%JRE%\jre" SET JRE=%JDK%\jre
IF EXIST "%JRE%\lib\amd64" SET BITS=64

:: ---------------------------------------------------------------------
:: Collect JVM options and properties.
:: ---------------------------------------------------------------------
IF NOT "%@@product_uc@@_PROPERTIES%" == "" SET IDE_PROPERTIES_PROPERTY="-Didea.properties.file=%@@product_uc@@_PROPERTIES%"

SET USER_VM_OPTIONS_FILE=%USERPROFILE%\.@@system_selector@@\@@vm_options@@.vmoptions
SET VM_OPTIONS_FILE=%IDE_BIN_DIR%\@@vm_options@@.vmoptions
IF EXIST "%IDE_BIN_DIR%\win\@@vm_options@@.vmoptions" SET VM_OPTIONS_FILE=%IDE_BIN_DIR%\win\@@vm_options@@.vmoptions
IF EXIST %USER_VM_OPTIONS_FILE% SET VM_OPTIONS_FILE=%USER_VM_OPTIONS_FILE%
IF NOT "%@@product_uc@@_VM_OPTIONS%" == "" SET VM_OPTIONS_FILE=%@@product_uc@@_VM_OPTIONS%

SET ACC=
FOR /F "usebackq delims=" %%i IN ("%VM_OPTIONS_FILE%") DO CALL "%IDE_BIN_DIR%\append.bat" "%%i"
IF EXIST "%VM_OPTIONS_FILE%" SET ACC=%ACC% -Djb.vmOptionsFile="%VM_OPTIONS_FILE%"

SET COMMON_JVM_ARGS="-XX:ErrorFile=%USERPROFILE%\java_error_in_@@product_uc@@_%%p.log" "-XX:HeapDumpPath=%USERPROFILE%\java_error_in_@@product_uc@@.hprof" "-Xbootclasspath/a:%IDE_HOME%/lib/boot.jar" -Didea.paths.selector=@@system_selector@@ %IDE_PROPERTIES_PROPERTY%
SET IDE_JVM_ARGS=@@ide_jvm_args@@
SET ALL_JVM_ARGS=%ACC% %COMMON_JVM_ARGS% %IDE_JVM_ARGS%

@@class_path@@
IF NOT "%@@product_uc@@_CLASS_PATH%" == "" SET CLASS_PATH=%CLASS_PATH%;%@@product_uc@@_CLASS_PATH%

:: ---------------------------------------------------------------------
:: Run the IDE.
:: ---------------------------------------------------------------------
SET OLD_PATH=%PATH%
SET PATH=%IDE_BIN_DIR%;%PATH%

"%JAVA_EXE%" %ALL_JVM_ARGS% -cp "%CLASS_PATH%" com.intellij.idea.Main %*

SET PATH=%OLD_PATH%
