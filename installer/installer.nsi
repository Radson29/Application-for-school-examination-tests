!include nsDialogs.nsh
!include LogicLib.nsh
!include MUI2.nsh

Name "Quizzes Installer"
Outfile "QuizzesInstaller.exe"
RequestExecutionLevel admin
ShowInstDetails show
InstallDir $PROGRAMFILES\Quizzes

Var Dialog
Var SeedOption
Var UseDefaultSettings
Var DBHostname
Var DBUsername
Var DBPassword
Var DBPort
Var DBName
Var isDbRemote

Page custom pgSeedPageCreate pgSeedPageLeave
Page custom pgSettingsPageCreate pgSettingsPageLeave

!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_LANGUAGE "English"

Function pgSeedPageCreate
    !insertmacro MUI_HEADER_TEXT "Database Seed" "Choose if you want to seed the database."

    nsDialogs::Create 1018
    Pop $Dialog

    ${If} $Dialog == error
        Abort
    ${EndIf}

    ${NSD_CreateGroupBox} 10% 10u 80% 62u "Database Seed Option"
    Pop $0

    ${NSD_CreateRadioButton} 20% 26u 60% 10u "Do not seed the database"
    Pop $SeedOption
	${NSD_Check} $SeedOption
	
    ${NSD_CreateRadioButton} 20% 40u 60% 10u "Seed the database"
    Pop $SeedOption

    nsDialogs::Show
FunctionEnd

Function pgSeedPageLeave
    ${NSD_GetState} $SeedOption $SeedOption
    StrCpy $SeedOption "$SeedOption"
FunctionEnd

Function pgSettingsPageCreate
    !insertmacro MUI_HEADER_TEXT "Database Connection Settings" "Choose if you want to use default settings or enter custom connection information."

    nsDialogs::Create 1018
    Pop $Dialog

    ${If} $Dialog == error
        Abort
    ${EndIf}

	${NSD_CreateCheckbox} 0 0 50% 6% "Use remote database:"
		Pop $isDbRemote
		${NSD_OnClick} $isDbRemote EnDisableButton

    ${NSD_CreateLabel} 20% 30u 20% 10u "Hostname:"
    Pop $0
    ${NSD_CreateText} 40% 30u 40% 12u ""
    Pop $DBHostname
	EnableWindow $DBHostname 0
	${NSD_SetText} $DBHostname "127.0.0.1"

    ${NSD_CreateLabel} 20% 45u 20% 10u "Username:"
    Pop $0
    ${NSD_CreateText} 40% 45u 40% 12u ""
    Pop $DBUsername
	EnableWindow $DBUsername 0
	${NSD_SetText} $DBUsername "root"

    ${NSD_CreateLabel} 20% 60u 20% 10u "Password:"
    Pop $0
    ${NSD_CreatePassword} 40% 60u 40% 12u ""
    Pop $DBPassword
	EnableWindow $DBPassword 0
	${NSD_SetText} $DBPassword ""

    ${NSD_CreateLabel} 20% 75u 20% 10u "Port:"
    Pop $0
    ${NSD_CreateText} 40% 75u 40% 12u ""
	Pop $DBPort
	EnableWindow $DBPort 0
	${NSD_SetText} $DBPort "3306"

    ${NSD_CreateLabel} 20% 90u 20% 10u "Database Name:"
    Pop $0
    ${NSD_CreateText} 40% 90u 40% 12u ""
	Pop $DBName
	EnableWindow $DBName 0
	${NSD_SetText} $DBName "quiz_server"

    nsDialogs::Show
FunctionEnd

Function pgSettingsPageLeave
    ${NSD_GetState} $UseDefaultSettings $UseDefaultSettings
    ${NSD_GetText} $DBHostname $DBHostname
    ${NSD_GetText} $DBUsername $DBUsername
    ${NSD_GetText} $DBPassword $DBPassword
    ${NSD_GetText} $DBPort $DBPort
    ${NSD_GetText} $DBName $DBName

    StrCpy $DBHostname "$DBHostname"
    StrCpy $DBUsername "$DBUsername"
    StrCpy $DBPassword "$DBPassword"
    StrCpy $DBPort "$DBPort"
    StrCpy $DBName "$DBName"
FunctionEnd

Function EnDisableButton
	Pop $isDbRemote
	${NSD_GetState} $isDbRemote $0
	${If} $0 == 1
		EnableWindow $DBHostname 1
		EnableWindow $DBUsername 1
		EnableWindow $DBPassword 1
		EnableWindow $DBPort 1
		EnableWindow $DBName 1
	${Else}
		EnableWindow $DBHostname 0
		EnableWindow $DBUsername 0
		EnableWindow $DBPassword 0
		EnableWindow $DBPort 0
		EnableWindow $DBName 0
	${EndIf}
FunctionEnd

Section
	SetOutPath "$INSTDIR"

	; Open the file for writing
	FileOpen $0 "$INSTDIR\db_config.txt" w

	; Write variable names and values to the file
	FileWrite $0 "db_ip=$DBHostname$\r$\n"
	FileWrite $0 "db_port=$DBPort$\r$\n"
	FileWrite $0 "db_name=$DBName$\r$\n"
	FileWrite $0 "db_user=$DBUsername$\r$\n"
	FileWrite $0 "db_password=$DBPassword$\r$\n"
	FileWrite $0 "db_seed=$SeedOption$\r$\n"

	; Close the file
	FileClose $0

	File "quiz.ico"
    File "xampp-windows-x64-8.1.2-0-VS16-installer.exe"
    File "jdk-18_windows-x64_bin.exe"
    File "School Quizzes Setup 0.1.0.exe"
	File "QuizServer.jar"
	File "Powerlauncher.exe"

    ExecWait '"$INSTDIR\xampp-windows-x64-8.1.2-0-VS16-installer.exe" --mode unattended --launchapps 0'
	ExecWait '"$INSTDIR\jdk-18_windows-x64_bin.exe" INSTALL_SILENT=1 STATIC=0 AUTO_UPDATE=0 WEB_ANALYTICS=0 REBOOT=0 SPONSORS=0 /s'
	ExecWait '"$INSTDIR\School Quizzes Setup 0.1.0.exe"'
	
	CreateShortcut "$DESKTOP\School Quizzes.lnk" "$INSTDIR\PowerLauncher.exe" "" "$INSTDIR\quiz.ico" 0
	
	Delete "$INSTDIR\xampp-windows-x64-8.1.2-0-VS16-installer.exe"
	Delete "$INSTDIR\jdk-18_windows-x64_bin.exe"
	Delete "$INSTDIR\School Quizzes Setup 0.1.0.exe"
	
	WriteUninstaller "$INSTDIR\uninstall.exe"
SectionEnd

Section "Uninstall"
  RMDir "$INSTDIR"
SectionEnd
