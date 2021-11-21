$pomVersion = ([xml](Get-Content .\pom.xml)).project.version
((Get-Content -path .\build_scripts\MineOnline.app\Contents\info.plist -Raw) -replace '{version}', $pomVersion) | Set-Content -Path .\build_scripts\MineOnline.app\Contents\info.plist
cp "target/MineOnline-$pomVersion.jar" "/home/appveyor/projects/mineonline/build_scripts/MineOnline.app/Contents/Java/MineOnline-$pomVersion.jar"
$compress = @{
  Path = ".\build_scripts\MineOnline.app"
  CompressionLevel = "Fastest"
  DestinationPath = ".\target\MineOnline-$pomVersion.zip"
}
Compress-Archive @compress