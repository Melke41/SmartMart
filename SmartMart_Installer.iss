[Setup]
AppName=SmartMart
AppVersion=1.0
AppPublisher=Melkamu Abyot, Samuel Alemayehu, Mengistu Tark
AppPublisherURL=https://github.com/Melke41/SmartMart
DefaultDirName={autopf}\SmartMart
DefaultGroupName=SmartMart
OutputDir=release
OutputBaseFilename=SmartMart_Setup
Compression=lzma
SolidCompression=yes
WizardStyle=modern
SetupIconFile=docs\smartmart.ico
UninstallDisplayIcon={app}\SmartMart.jar

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "database\schema.sql"; DestDir: "{app}\database"; Flags: ignoreversion
Source: "database\seed.sql"; DestDir: "{app}\database"; Flags: ignoreversion
Source: "SmartMart.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "docs\smartmart.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "release\HOW_TO_RUN.txt"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\SmartMart"; Filename: "{app}\jre\bin\javaw.exe"; Parameters: "-jar ""{app}\SmartMart.jar"""; IconFilename: "{app}\smartmart.ico"
Name: "{commondesktop}\SmartMart"; Filename: "{app}\jre\bin\javaw.exe"; Parameters: "-jar ""{app}\SmartMart.jar"""; IconFilename: "{app}\smartmart.ico"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Create a desktop shortcut"; GroupDescription: "Additional icons:"

[Run]
Filename: "{app}\jre\bin\javaw.exe"; Parameters: "-jar ""{app}\SmartMart.jar"""; Description: "Launch SmartMart"; Flags: nowait postinstall skipifsilent

[Code]
function InitializeSetup(): Boolean;
begin
  Result := True;
end;
