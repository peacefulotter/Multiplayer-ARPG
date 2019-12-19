Game
## Movement
#### Dash
[PRESS A] : the player makes a dash (moves quickly and forward by a couple of cells)
## Area
#### House Background
The player can enter inside the house at the spawn
   - file is located at images/backgrounds/custom/House.png
#### House Behavior
The house has walls thanks to the House Behavior Image
   - file is located at images/behaviors/custom/House.png
## Inventory items
#### Mouse Wheel
Added the mouse wheel to switch items (using tab still works)
## Battles
#### Crits
The player has a certain percentage of chance to deal "crits" ("critical damage") to any monster he fights
A little image is shown above the monster when dealing a crits



# Multiplayer
## General architecture
![General Architecture](architecture.png)
    
## Game thread
Both the server and the client have their own game thread that runs the runnable ThreadedPlay, this is so that we 
don't have to worry about our socket communication blocking the game.
### NARPG
NARPG(Networked Role Playing Game) is the networked version of ARPG which has a list of players instead of a single one,
implements some important methods needed for a networked game and currently only uses
a single area.

NARPG keeps track if it is currently run on the server or client side (isServer) and of the current Connection (will be explained later) 

##### begin (Window window, Filesystem filesystem)
the begin method creates new areas(currently only a single one), and sets the current area 
to our custom NetworkArena area.

If the current game is run on the client side, it will tell the client to login (explained later)
and spawn a new NetworkARPGPlayer with clientAuthority set to true, this will allow the 
the client to control this instance of NetworkARPGPlayer.
##### updateObject(Packet03Update update)
finds the entity that needs updating and passes the updateState HashMap to that entity
##### moveObject(Packet02Move packet)
finds correct MovableNetworkEntity and sends it the move packet.
##### spawnObject(Packet00Spawn packet)
handles spawning of network entities : registers the correct entity to the current area
and in case of PLAYER will also add it to leftToRegister if registerActor returns false.
leftToRegister acts as a sort of buffer which registers the remaining players as soon as possible
##### login()
on the login of a new client the server will send spawn packets of every network entity 
to all clients to make sure everything is up to date.
##### update(float deltaTime)
calls super.update(deltaTime) and then registers a maximum of one player at a time
im leftToRegister

Also removes queued network entities that need to be removed (leftToUnregisters). This
is to avoid ConcurrencyModificationException

### Networked entities
All network entities implement either NetworkEntity or MovableNetworkEntity, which extends
NetworkEntity which itself extends Actor. All NetworkEntity's have an id that is assumed 
to be unique (hopefully, no checks are made).

Network entities are stores in the NetworkArena which has it's own registerActor and 
unregisterActor to keep track of network entitites.
#### NetworkARPGPlayer
NetworkARPGLPlayer is the most complicated of all the networked entities, as it needs
to take into account player input and send it to the server. It extends ARPGPlayer and
implements MovableNetworkEntity

Each client has their own NetworkARPGPlayer that they control : this is determined by
the boolean clientAuthority.

NetworkARPGLPlayer also has an additional TextGraphics property which displays the
controlling client's username.

To avoid sending too many update packets, changes in state are stored in queuedUpdates
##### update(float deltaTime)
queuedUpdates will be emptied once every update by sending a corresponding Packet03Update
next, if client has clientAuthority over this instance it will react to client input
and send corresponding packets.

calls super.update() at the end
##### useItem()
ARPGPlayer's useItem is overriden to spawn corresponding network entitis instead of the
normal ones. Ex spawn networkedBomb instead of Bomb
######NetworkMove(Packet02Move movePacket)
As by the MovableNetworkEntity this allows the server to move the entity.
######NetworkARPGPlayerHandler 

NetworkARPGPlayer has it's own interaction handler that also implements NARPGInteractionVisitor
 as the interactions with networked versions of the main game differ from the single-player version.
 
####NetworkArrow
The network arrow is also a MovableNetworkEntity.

It is quite similar to the Arrow projectile in ARPG, except that it only interacts on the server
side and then sends corresponding updates to the clients.
##Networking

#####Connection Interface
The connection interface is implemented by both the Server and Client.

###Server

most importantly the server class keeps a list of all it's active connections and handles
sending data to all or a single connection. It keeps listening for new connections and
creates a new ConnectionHandler thread when it makes a new connection.

###Client

Has a single ConnectionHandler that it uses to communicate with the Server. Also handles login once 
connection has been made.

###ConnectionHandler

Handles sending, receiving and basic data processing between Server and Client.
It sends data in bytes and decodes it using processIncomingData and calls
the corresponding function in NARPG for every valid Packet

Also checks for time out of connection and stops the thread if there is an error or
no connection for too long.

Usually sends all data received from one client to all others.
###Packets
Packets provide basic functionality to serialize and deserialize different events or state
changes happening to specific network entities.

Each packet type is identified by the first two bytes as they each accomplish a specific task
#####Packet00Spawn  
spawns a NetworkEntity. Uses NetworkEntities enum to serialize which of type of network
entity needs to be spawned and initialState to set the state of the entity on spawn.
#####Packet01Login
Used by client to initialise correct ConnectionHandler connectionId and to request the 
SpawnPackets of all current networkentities to get up to date with the server state.
#####Packet02Move
Used to move the player, only affects player instanes that do not have client authority.
#####Packet03Update
Uses HashMap<String,String> to store state changes as hash maps containing strings for 
both the key and value can easily be serialized.
#####Packet04Chat 
Doesn't have an objectId and has a string as it's only argument. Just adds an announcement 
to the ServerAnnouncements.
#####Packet05Logout
Used to indicate the closure of a ConnectionHandler and handles removing of the corresponding
player
#####Packet06Despawn
Despawns a network entity. Removes it from networkEntitys and unregisters it from 
the NetworkArena area
## Movement
 - dash [PRESS A] : the player makes a dash (move quickly and forward by a couple of cells)

## Area
 - House : the player can enter inside the house at the spawn
   - file is located at images/backgrounds/custom/House.png
 - House Behavior : the house has walls thanks to the House Behavior Image
   - file is located at images/behaviors/custom/House.png

## Inventory items
 - Added the mouse wheel to switch items (using tab still works)
###Miscellaneous

- Modified window dispose to dispatch window closing event
-


