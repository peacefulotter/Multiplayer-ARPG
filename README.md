# MP2
EPFL - Mini-Project 2 

### todo list
 - [fix] DarkLord tp not working properly (creates empty and non traversable space) 
 - [fix] fire damage player
 - [fix] Make a interface "Damageable" or something like that for all the entities that can deal damage
 - [fix] flame skull attacking only once
 - [fix] Bomb multiple placement (canHoldDowm - mve to arpgItem)
 - [fix] Player go in both directions 
 - [add] Add inside house
 - [add] Bridge + maagic wand there
 - [add] Inventory display
 
 #Multiplayer
 ##Server
 We created a custom tcp server that can be hosted locally or be put on a remote server
###Local hosting
to host a local version of the server one just has to run the Server.java file as the main class with the port as the only program argument
###remote hosting
 We hosted the game server on a remote ubuntu server (epflmp2.me)  by first exporting the server as a jar
 file and by running this command on the remote server :
```shell script
java -Djava.awt.headless=true -jar <serverJarName> & 
```
this command runs the server as a background program. To not run it as a background program use the command without "&" at the end.
##### requirements for remote hosting
as the remote server doesn't have a display we need to run the game with java.awt.headless set to true. 
This runs the java program in headless mode meaning we can't use JFrames.
##### manageing a remote server
the command mentioned above runs the server as a background program which makes managing the program quite a hassle.
To help with one can create a service (on Linux):
```shell script
[Unit]
Description=gameServer Daemon

[Service]
ExecStart=/usr/bin/java -Djava.awt.headless=true -jar /root/Server 4000
User=root

[Install]
WantedBy=multi-user.target
```
this way all that one has to do is use these commands to run/stop the server:

```shell script
systemctl start serversservice
systemctl stop serversservice
```

##Client
The client connects

