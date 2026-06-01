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
Source: "SmartMart.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "database\schema.sql"; DestDir: "{app}\database"; Flags: ignoreversion
Source: "database\seed.sql"; DestDir: "{app}\database"; Flags: ignoreversion
Source: "database\init_db.py"; DestDir: "{app}\database"; Flags: ignoreversion
Source: "release\HOW_TO_RUN.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "docs\smartmart.ico"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\SmartMart"; Filename: "{app}\SmartMart.jar"; IconFilename: "{app}\smartmart.ico"
Name: "{commondesktop}\SmartMart"; Filename: "{app}\SmartMart.jar"; IconFilename: "{app}\smartmart.ico"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Create a desktop shortcut"; GroupDescription: "Additional icons:"

[Run]
Filename: "python"; Parameters: "{app}\database\init_db.py"; Description: "Initialize database"; Flags: runhidden waituntilterminated
Filename: "{app}\SmartMart.jar"; Description: "Launch SmartMart"; Flags: nowait postinstall skipifsilent shellexec

[Code]
function InitializeSetup(): Boolean;
var
  JavaPath: String;
begin
  if not RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', JavaPath) then
  begin
    MsgBox('Java is not installed. Please install Java 8 or higher from https://java.com before installing SmartMart.', mbError, MB_OK);
    Result := False;
  end else
    Result := True;
end;
