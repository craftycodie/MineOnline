$pomVersion = ([xml](Get-Content .\pom.xml)).project.version
((Get-Content -path .\build_scripts\MineOnline.app\Contents\info.plist -Raw) -replace '{version}', $pomVersion) | Set-Content -Path .\build_scripts\MineOnline.app\Contents\info.plist
New-Item -ItemType "directory" -Path ".\build_scripts\MineOnline.app\Contents\Java"
Copy-Item ".\target\MineOnline-$pomVersion.jar" -Destination ".\build_scripts\MineOnline.app\Contents\Java\MineOnline-$pomVersion.jar"
$compress = @{
  Path = ".\build_scripts\MineOnline.app"
  CompressionLevel = "Fastest"
  DestinationPath = ".\target\MineOnline-$pomVersion.zip"
}
Compress-Archive @compress