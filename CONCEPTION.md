# Multiplayer
## General architecture
### Game thread
Both the server and the client have their own game thread that runs the runnable ThreadedPlay, this is so that we 
don't have to worry about our socket communication blocking the game.
### NARPG
NARPG(Networked Role Playing Game) is the networked version of ARPG which has a list of players instead of a single one,
implements some important methods needed for a networked game and currently only uses
a single area.

NARPG keeps track if it is currently run on the server or client side (isServer) and of the current Connection (will be explained later) 

In addition, NARPG keeps track of all the current NetworkEntities

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

### Networked entities
All network entities implement either NetworkEntity or MovableNetworkEntity, which extends
NetworkEntity which itself extends Actor. All NetworkEntity's have an id that is assumed 
to be unique (hopefully, no checks are made).
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
##### NetworkMove(Packet02Move movePacket)
teleports the player

## Movement
 - dash [PRESS A] : the player makes a dash (move quickly and forward by a couple of cells)

## Area
 - House : the player can enter inside the house at the spawn
   - file is located at images/backgrounds/custom/House.png
 - House Behavior : the house has walls thanks to the House Behavior Image
   - file is located at images/behaviors/custom/House.png

## Inventory items
 - Added the mouse wheel to switch items (using tab still works)


