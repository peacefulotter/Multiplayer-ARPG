# MP2
EPFL - Mini-Project 2 

## Inputs
 - [ARROW KEY] Move the player
 - [E] Interact
 - [SPACE] Use equipped item
 - [TAB / MOUSE WHEEL] Switch equipped item
 - [A] Dash
 - [U / I / O / P] In multiplayer mode, select the upgrade of the bow or arrows
 - [Y] In multiplayer mode, send a message to everyone "J'aime ce jeu!"
 
 # Multiplayer
 ## Server
 We created a custom tcp server that can be hosted locally or be put on a remote server
### Local hosting
to host a local version of the server one just has to run the Server.java file as the main class with the port as the only program argument
### Remote hosting
 We hosted the game server on a remote ubuntu server (epflmp2.me)  by first exporting the server as a jar
 file and by running this command on the remote server :
```shell script
java -Djava.awt.headless=true -jar <serverJarName> & 
```
this command runs the server as a background program. To not run it as a background program use the command without "&" at the end.
##### Requirements for remote hosting
as the remote server doesn't have a display we need to run the game with java.awt.headless set to true. 
This runs the java program in headless mode meaning we can't use JFrames.
##### Manageing a remote server
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

## Client
The client connects to the tcp server by using the address, the port and username as program arguments
##### Connecting to our remote server
To connect to our remote server use the following program arguments:
```shell script
epflmp2.me 4000 <your_chosen_username>
```
##### Connecting to your local server
To connect to your own local server use the following program arguments:
```shell script
localhost <your_chosen_port> <your_chosen_username>
```
## Multiplayer gameplay
