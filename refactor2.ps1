$ErrorActionPreference = "Stop"
$path = "d:\NaturalSMP\plugin\NaturalWorldGen"

Write-Host "Renaming files and directories..."

$files = Get-ChildItem -Path $path -Recurse -File | Where-Object {
    $_.Extension -in @(".java", ".kt", ".kts", ".xml", ".yml", ".json", ".md", ".txt", ".properties", ".gradle", ".ignore") -or $_.Name -eq "gradlew" -or $_.Name -eq "gradlew.bat"
}

foreach ($file in $files) {
    if ($file.FullName -match "\\\.git\\") { continue }
    
    $content = Get-Content $file.FullName -Raw
    $original = $content
    
    # Pre-emptively fix aliases and commands
    $content = $content -creplace "aliases: \[ ir, irs \]", "aliases: [ nwg, natural ]"
    $content = $content -creplace "commands:\n  iris:", "commands:`n  nwg:"
    $content = $content -creplace "commands:\r\n  iris:", "commands:`r`n  nwg:"
    $content = $content -creplace '"/iris"', '"/nwg"'
    $content = $content -creplace '"/iris ', '"/nwg '
    $content = $content -creplace '"iris"', '"nwg"'
    $content = $content -creplace "volmit\.com", "naturalsmp.id"
    $content = $content -creplace "VolmitSoftware/Iris", "Natural-Minecraft/NaturalWorldGen"
    $content = $content -creplace "com\.volmit\.iris", "id.naturalsmp.NaturalWorldGen"
    $content = $content -creplace "com/volmit/iris", "id/naturalsmp/NaturalWorldGen"
    
    # Class and identifier renaming
    $content = $content -creplace "class Iris\b", "class NaturalGenerator"
    $content = $content -creplace "public Iris\b", "public NaturalGenerator"
    $content = $content -creplace "Iris\.class", "NaturalGenerator.class"
    $content = $content -creplace "Iris\.instance", "NaturalGenerator.instance"
    
    $content = $content -creplace "Volmit", "NaturalDev"
    $content = $content -creplace "volmit", "naturalsmp" # usually in URLs / lowercase names
    
    # Main Iris -> NaturalWorldGen text replacement
    $content = $content -creplace "\bIris\b", "NaturalWorldGen"
    $content = $content -creplace "\biris\b", "naturalworldgen"

    if ($content -cne $original) {
        #Write-Host "Updated $($file.FullName)"
        [IO.File]::WriteAllText($file.FullName, $content, (New-Object System.Text.UTF8Encoding($false)))
    }
}

Write-Host "1. Text replaced in all files."

# Now rename Iris.java to NaturalGenerator.java
$javaFiles = Get-ChildItem -Path $path -Recurse -File -Filter "Iris*.java"
foreach ($f in $javaFiles) {
    $newName = $f.Name -replace "Iris", "NaturalGenerator"
    Rename-Item -Path $f.FullName -NewName $newName -PassThru | Out-Null
}
$ktFiles = Get-ChildItem -Path $path -Recurse -File -Filter "Iris*.kt"
foreach ($f in $ktFiles) {
    $newName = $f.Name -replace "Iris", "NaturalGenerator"
    Rename-Item -Path $f.FullName -NewName $newName -PassThru | Out-Null
}

# The hard part: moving com/volmit/iris to id/naturalsmp/NaturalWorldGen
# We will find every folder named 'iris' that has 'volmit' as parent
$irisFolders = Get-ChildItem -Path $path -Recurse -Directory | Where-Object { $_.Name -eq "iris" -and $_.Parent.Name -eq "volmit" }

foreach ($folder in $irisFolders) {
    $parentCom = $folder.Parent.Parent
    if ($parentCom.Name -eq "com") {
        # We need to create id/naturalsmp/NaturalWorldGen relative to parentCom.Parent
        $basePath = $parentCom.Parent.FullName
        $newDir = Join-Path $basePath "id\naturalsmp\NaturalWorldGen"
        New-Item -ItemType Directory -Force -Path $newDir | Out-Null
        
        # Move contents of $folder to $newDir
        Move-Item -Path "$($folder.FullName)\*" -Destination $newDir -Force
        
        # Clean up empty com/volmit/iris, com/volmit, com
        Remove-Item -Path $folder.FullName -Force -Recurse
        if ((Get-ChildItem -Path $folder.Parent.FullName).Count -eq 0) { Remove-Item -Path $folder.Parent.FullName -Force -Recurse }
        if ((Get-ChildItem -Path $parentCom.FullName).Count -eq 0) { Remove-Item -Path $parentCom.FullName -Force -Recurse }
    }
}

Write-Host "Done Refactoring!"
