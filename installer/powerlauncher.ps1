# Get db variable names and values from the file
$values = Get-Content -Path "db_config.txt"

foreach ($line in $values) {
    $variable = $line.Split('=')[0].Trim()
    $value = $line.Split('=')[1].Trim()

    Set-Variable -Name $variable -Value $value
}

if ($db_seed -eq "1") {
    $db_seed = "true"
} else {
    $db_seed = "false"
}

# MySQL
$mysql_process_id = Get-NetTCPConnection | Where-Object { $_.LocalPort -eq 3306 -and $_.OwningProcess -ne 0 } | Select-Object -ExpandProperty OwningProcess -First 1

if (-not $mysql_process_id) {
	Write-Output "Starting MySQL server ..."
	Start-Process -WindowStyle hidden "C:\xampp\mysql_start.bat"

	do {
		Start-Sleep -Seconds 3
		$mysql_process_name = Get-Process -Name "mysqld" -ErrorAction SilentlyContinue
	} while (-not $mysql_process_name)

	Write-Output "MySQL server started successfully."
}
else {
	Write-Output "MySQL process already running. Skipping ..."
}

# Setup Database
$mysql_createdb_command = '& "C:\xampp\mysql\bin\mysql" -u root -e "CREATE DATABASE quiz_server;"'

try {
	Invoke-Expression -Command $mysql_createdb_command
	Write-Output 'Database created successfully.'
} catch {
	Write-Output "An error occurred: $_"
}

# Server
$server_process_id = Get-NetTCPConnection | Where-Object { $_.LocalPort -eq 8080 -and $_.OwningProcess -ne 0 } | Select-Object -ExpandProperty OwningProcess -First 1

if (-not $server_process_id) {
	Write-Output "Starting the Server ..."
	
	$server_arguments_list = "-jar QuizServer.jar --db.seed=$db_seed --db.ip=$db_ip --db.port=$db_port --db.name=$db_name --db.user=$db_user --db.password=$db_password"

	Start-Process -WindowStyle hidden "java" -ArgumentList $server_arguments_list
	
	do {
		Start-Sleep -Seconds 3
		$server_process_name = Get-Process -Name "java" -ErrorAction SilentlyContinue
	} while (-not $server_process_name)

	Write-Output "Server started successfully."
}
else {
	Write-Output "Server process already running. Skipping ..."
}

$frontend_process_name = Get-Process -Name "School Quizzes" -ErrorAction SilentlyContinue

if (-not $frontend_process_name) {
	Start-Process -FilePath "$env:LocalAppData\Programs\quizzes_frontend\School Quizzes.exe" -ErrorAction SilentlyContinue | out-null
	Write-Output "Frontend started successfully."
}
else {
	Write-Output "Frontend process already running. Skipping ..."
}	
