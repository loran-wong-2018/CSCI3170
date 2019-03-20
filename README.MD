## Prerequisite
1. Connect to CSE VPN
Refer to the powerpoint slides of tutorial 5

2. Download SSH Secure Shell

## SSH Secure Shell

#### Connect to Linux Server

HOSTNAME : linux1.cse.cuhk.edu.hk  
USERNAME : db023  
PORT     : 22  
PASSWORD : qaksa3vq  

##### Connect to MySQL

mysql --host=projgw --port=2633 -u Group28 –p  
CSCI3170

use db28;

## Command Line Interface
##### Compile the java program files if it has been changed
1. Download the java jdk program to use the javac function.  
2. Set the path so that the javac function can be run. (the code below differs from computer)  
`set PATH=%PATH%;C:\Program Files\Java\jdk1.8.0_181\bin`  
3. As an example, the code below compiles the admin.java program.  
`javac admin.java`  
In case of error, this thread may be useful https://stackoverflow.com/questions/15575801/javac-is-not-found

##### Run the program
As an example, the code below runs the admin.java program  
`java -cp .;C:\Users\LoranWong\Desktop\CSCI3170\mysql-connector-java-5.1.47\mysql-connector-java-5.1.47.jar admin`