$pomVersion = ([xml](Get-Content .\pom.xml)).project.version
((Get-Content -path .\build_scripts\MineOnline.app\Contents\info.plist -Raw) -replace '{version}', $pomVersion) | Set-Content -Path .\build_scripts\MineOnline.app\Contents\info.plist
Copy-Item ".\target\MineOnline-$pomVersion.jar" -Destination ".\build_scripts\MineOnline.app\Contents\Java\"
$compress = @{
  Path = ".\build_scripts\MineOnline.app"
  CompressionLevel = "Fastest"
  DestinationPath = ".\target\MineOnline-$pomVersion.zip"
}
Compress-Archive @compress