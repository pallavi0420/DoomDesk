# PowerShell script – build signed release AAB with JBR (Java 21)
$possibleJbr = @(
    'C:\Program Files\Android\Android Studio1\jbr',
    'C:\Program Files\Android\Android Studio\jbr'
)
$jbr = $possibleJbr | Where-Object { Test-Path (Join-Path $_ 'bin\java.exe') } | Select-Object -First 1
$javaExe = Join-Path $jbr 'bin\java.exe'

if (-Not (Test-Path $javaExe)) {
    Write-Error "JBR java.exe not found at $javaExe"
    exit 1
}

$env:JAVA_HOME = $jbr

# Run Gradle wrapper JAR directly with JBR
& (Join-Path $jbr 'bin\java.exe') -jar .\gradle\wrapper\gradle-wrapper.jar bundleRelease --no-daemon --stacktrace
