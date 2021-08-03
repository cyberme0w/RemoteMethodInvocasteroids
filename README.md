# RMIProject
Small program using Java's RMI (Remote Methode Invocation) to write a server/client multiplayer version of the popular game "Asteroids".

## How to run
* Clone the repository, add exec rights (```chmod +x run```) and run with ```./run```.
* Alternatively, start the RMI, Server and clients by cd'ing into the folder and running the following commands:
  * Compile Java: ```javac *.java```
  * RMI: ```rmiregistry &```
  * Server: ```java -Djava.rmi.server.hostname=YOUR.IP.ADDRESS.HERE -Djava.rmi.server.codebase="file:$PWD" AsteroidsServer```
  * Client: ```java AsteroidsClient "YOUR.IP.ADDRESS.HERE"```

## How to play
Stay alive by dodging the asteroids or blowing them up with your lasers, but beware - blown up asteroids will split up into smaller pieces!
Use A/D to turn the ship, W to accelerate and J to [insert functionality here].

The point system is both time and enemy based.
For every second you remain alive, ```X``` points will be added to your score.
Also, for each asteroid you destroy you will be awarded ```10/15/25/50``` points, based on its size (largest to smallest).

# How does RMI work
## Remote Methode Interface
This interface serves as a reference for the local program. It sums up the available commands on the RMI Server that the client might access, and as such must always be up to date with the server.

## Implementation Class (stub)
This class is the so called "stub". The stub is used by the client as if it were an object representing the server. By using the stub just like one would use any other object, the stub speaks and listens to the server in order to give commands / retrieve answers.

## RMI Server
The server where the remote methods are stored.

## RMI Client
The client handles mostly GUI related things, but it also handles the timer logic.

