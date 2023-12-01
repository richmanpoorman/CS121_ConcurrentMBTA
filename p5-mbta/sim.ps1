
$ConfigFile = Read-Host "Configuration File"
java -cp "hamcrest-2.2.jar;junit-4.13.2.jar;gson-2.10.1.jar;." Sim $ConfigFile