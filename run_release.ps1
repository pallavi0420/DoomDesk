# PowerShell script – build signed release AAB with JBR (Java 21)
$jbr = 'C:\Program Files\Android\Android Studio\jbr'
$javaExe = Join-Path $jbr 'bin\java.exe'

if (-Not (Test-Path $javaExe)) {
    Write-Error "JBR java.exe not found at $javaExe"
    exit 1
}

$env:JAVA_HOME = $jbr

# Run Gradle wrapper JAR directly with JBR
& $javaExe -jar .\gradle\wrapper\gradle-wrapper.jar clean bundleRelease --no-daemon --stacktrace
