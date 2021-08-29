$pomVersion = ([xml](Get-Content .\pom.xml)).project.version
Update-AppveyorBuild -Version "$pomVersion.$env:APPVEYOR_BUILD_NUMBER"