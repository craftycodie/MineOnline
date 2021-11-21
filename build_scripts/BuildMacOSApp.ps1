$pomVersion = ([xml](Get-Content .\pom.xml)).project.version
((Get-Content -path .\build_scripts\MineOnline.app\Contents\Info.plist -Raw) -replace '{version}', $pomVersion) | Set-Content -Path .\build_scripts\MineOnline.app\Contents\Info.plist
Copy-Item "MineOnline-$pomVersion.jar" -Destination ".\build_scripts\MineOnline.app\Contents\Java"
$compress = @{
  Path = ".\build_scripts\MineOnline.app"
  CompressionLevel = "Fastest"
  DestinationPath = "MineOnline-$pomVersion.zip"
}
Compress-Archive @compress