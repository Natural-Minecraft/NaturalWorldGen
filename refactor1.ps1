$ErrorActionPreference = "Stop"
$path = "d:\NaturalSMP\plugin\NaturalWorldGen"

Write-Host "Renaming files and directories..."

# 1. Replace strings in all files
# We will target all text files
$files = Get-ChildItem -Path $path -Recurse -File | Where-Object {
    $_.Extension -in @(".java", ".kt", ".kts", ".xml", ".yml", ".json", ".md", ".txt", ".properties", ".gradle") -or $_.Name -eq "gradlew" -or $_.Name -eq "gradlew.bat"
}

foreach ($file in $files) {
    if ($file.FullName -match "\\\.git\\") { continue }
    
    $content = Get-Content $file.FullName -Raw
    $original = $content
    
    # Custom replacements
    $content = $content -replace "com\.volmit\.iris", "id.naturalsmp.NaturalWorldGen"
    $content = $content -replace "com/volmit/iris", "id/naturalsmp/NaturalWorldGen"
    # The plugin.yml has aliases
    $content = $content -replace "aliases: \[ ir, irs \]", "aliases: [ natural ]"
    $content = $content -replace "commands:\n  iris:", "commands:`n  nwg:"
    $content = $content -replace "commands:\r\n  iris:", "commands:`r`n  nwg:"
    $content = $content -replace '"iris"', '"nwg"'
    $content = $content -replace '"/iris"', '"/nwg"'
    $content = $content -replace '"/iris ', '"/nwg '
    $content = $content -replace "volmit\.com", "naturalsmp.id"
    
    # Generic Class Replacements
    $content = $content -replace "\bIris\b(?=\.java|\.class)", "NaturalGenerator"
    $content = $content -replace "\bclass Iris\b", "class NaturalGenerator"
    $content = $content -replace "\bpublic Iris\b", "public NaturalGenerator"
    $content = $content -replace "\bIris\.class\b", "NaturalGenerator.class"
    $content = $content -replace "\bIris\.instance\b", "NaturalWorldGen.instance"
    $content = $content -replace "\bIrisPlugin\b", "NaturalGenerator"
    
    $content = $content -replace "VolmitSoftware/Iris", "Natural-Minecraft/NaturalWorldGen"
    
    # Word boundaries to be safe
    # Replace "Iris" but NOT "Irises" etc, usually Java identifiers
    # We will use simple case-sensitive string replacement for Iris -> NaturalWorldGen
    # But wait, -replace in Powershell is case-insensitive by default !!
    # MUST USE -creplace !!
    
    $content = $content -creplace "com\.volmit\.iris", "id.naturalsmp.NaturalWorldGen"
    $content = $content -creplace "com/volmit/iris", "id/naturalsmp/NaturalWorldGen"
    $content = $content -creplace "volmit\.com", "naturalsmp.id"
    $content = $content -creplace "Volmit", "NaturalDev"
    $content = $content -creplace "volmit", "naturaldev"
    $content = $content -creplace "IrisPlugin", "NaturalGenerator"
    $content = $content -creplace "class Iris\b", "class NaturalGenerator"
    $content = $content -creplace "\bIris\b", "NaturalWorldGen"
    $content = $content -creplace "iris", "naturalworldgen"

    if ($content -cne $original) {
        Write-Host "Updated $($file.FullName)"
        [IO.File]::WriteAllText($file.FullName, $content, (New-Object System.Text.UTF8Encoding($false)))
    }
}

# 2. Rename directories recursively
# Because PowerShell can be tricky with moving parent/child concurrently, we sort by length descending to rename deepest folders first
$dirs = Get-ChildItem -Path $path -Recurse -Directory | Where-Object { $_.FullName -notmatch "\\\.git" } | Sort-Object -Property @{Expression={$_.FullName.Length}; Descending=$true}

foreach ($dir in $dirs) {
    if ($dir.Name -ceq "iris") {
        Rename-Item -Path $dir.FullName -NewName "naturalworldgen" -PassThru
    }
    if ($dir.Name -ceq "Iris") {
        Rename-Item -Path $dir.FullName -NewName "NaturalWorldGen" -PassThru
    }
    if ($dir.Name -ceq "volmit") {
        Rename-Item -Path $dir.FullName -NewName "naturaldev" -PassThru
    }
}

Write-Host "Done Refactoring Code Content and Directory Names"

# After that, we still need to fix the package structure since we just renamed 'volmit' to 'naturaldev' and 'iris' to 'naturalworldgen'
# So the path became: com/naturaldev/naturalworldgen. But it should be id/naturalsmp/NaturalWorldGen
