; MineOnline Windows installer.
; Installs the MineOnline jar with a bundled Java 8 runtime, so players don't need Java installed.
;
; Build with Inno Setup 6.3 or newer:
;   iscc /DAppVersion=4.9.0 /DJarFile=path\to\MineOnline-4.9.0.jar /DJreDir=path\to\jre installer\MineOnline.iss
;
; The launcher relaunches itself and the game with the Java that started it, so starting it with the
; bundled javaw.exe is enough for the bundled runtime to be used throughout.
; Player data (settings, versions, worlds) lives in %APPDATA%\.mineonline and is never touched by (un)installing.

#ifndef AppVersion
  #error AppVersion must be defined, e.g. /DAppVersion=4.9.0
#endif
#ifndef JarFile
  #error JarFile must be defined, e.g. /DJarFile=target\MineOnline-4.9.0.jar
#endif
#ifndef JreDir
  #error JreDir must be defined, e.g. /DJreDir=build\jre
#endif
#ifndef OutputDir
  #define OutputDir "..\target"
#endif

[Setup]
; Keep this AppId the same forever, it's how new versions find and upgrade an existing install.
AppId={{F394F75A-C9E6-4287-B131-2B139813D431}
AppName=MineOnline
AppVersion={#AppVersion}
AppVerName=MineOnline {#AppVersion}
AppPublisher=craftycodie
AppPublisherURL=https://github.com/craftycodie/MineOnline
AppSupportURL=https://github.com/craftycodie/MineOnline/issues
AppUpdatesURL=https://github.com/craftycodie/MineOnline/releases/latest
; Per-user install, no admin prompt. {autopf} resolves to %LOCALAPPDATA%\Programs for per-user installs.
PrivilegesRequired=lowest
DefaultDirName={autopf}\MineOnline
DefaultGroupName=MineOnline
DisableProgramGroupPage=yes
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
OutputDir={#OutputDir}
OutputBaseFilename=MineOnline-{#AppVersion}-installer
SetupIconFile=..\docs\icon.ico
UninstallDisplayIcon={app}\MineOnline.ico
UninstallDisplayName=MineOnline
WizardStyle=modern
Compression=lzma2/max
SolidCompression=yes
; Close a running MineOnline (it holds the jar open) before upgrading or uninstalling.
CloseApplications=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[InstallDelete]
; Remove the old runtime on upgrade, so files from a previous Java update don't linger.
Type: filesandordirs; Name: "{app}\jre"

[Files]
Source: "{#JarFile}"; DestDir: "{app}"; DestName: "MineOnline.jar"; Flags: ignoreversion
Source: "{#JreDir}\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\docs\icon.ico"; DestDir: "{app}"; DestName: "MineOnline.ico"; Flags: ignoreversion
Source: "..\License.txt"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{autoprograms}\MineOnline"; Filename: "{app}\jre\bin\javaw.exe"; Parameters: "-jar ""{app}\MineOnline.jar"""; WorkingDir: "{app}"; IconFilename: "{app}\MineOnline.ico"
Name: "{autodesktop}\MineOnline"; Filename: "{app}\jre\bin\javaw.exe"; Parameters: "-jar ""{app}\MineOnline.jar"""; WorkingDir: "{app}"; IconFilename: "{app}\MineOnline.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\jre\bin\javaw.exe"; Parameters: "-jar ""{app}\MineOnline.jar"""; WorkingDir: "{app}"; Description: "{cm:LaunchProgram,MineOnline}"; Flags: nowait postinstall skipifsilent
