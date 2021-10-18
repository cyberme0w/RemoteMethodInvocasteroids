# Remote Method InvocAsteroids
Program using Java's RMI (Remote Methode Invocation) to write a server/client cross-platform multiplayer version of the popular game "Asteroids".
It has currently been tested on Linux and Windows, but there should be no reason why it won't run on Apple devices.
The program is divided in 3 major components:
* The Registry, which binds remote objects to names, so they can be found by clients.
* The Server, which provides the methods and objects used in the game.
* The Client(s), which connect to the server and send commands to be executed by the server.

## How to play
To start the game, or reset after everyone has died, press `enter`.
Stay alive by dodging the asteroids or blowing them up with your lasers!
Use `A/D` to turn the ship, `W` to accelerate and `J` to give yourself a speed boost.
For each asteroid you destroy you will be awarded ```10/15/25/50``` points, based on its size (largest to smallest asteroids).
The winner is the player with the most points once everybody dies. Just like in real life.

## How to run
* Clone the repository
*  Linux:
  * `cd` into the repository, `chmod +x run` and `./run`
  * Alternatively, start each step manually:
    * Compile: `javac *.java`
    * Start the RMI Registry: `rmiregistry &`
    * Start the RMI Server (codebase = path_to_cloned_repo/AsteroidsServer): `java -Djava.rmi.server.hostname=0.0.0.0 -Djava.rmi.server.codebase=file:///PATH/TO/REPO/HERE/ AsteroidsServer`
    * Start Client(s):`java AsteroidsClient 0.0.0.0 username` 
* Windows:
  * `cd` into the repository and execute `runWindows.bat` (Note: might not always work --> alt mode is reliable though).
  * Alternatively, start each step manually:
    * Compile: `javac *.java`
    * Registry: `start rmiregistry`
    * Server: `java -D"java.rmi.server.hostname=0.0.0.0" -D"java.rmi.server.codebase=file:///PFAD/ZUM/REPO/HIER/" AsteroidsServer`
    * Client(s):`java AsteroidsClient 0.0.0.0 username`

## Known issues:
* On Linux (Debian) there seems to be graphical issues where FPS will drop drastically once the mouse stops moving over the client window. The game itself runs with no issues and good FPS once the mouse starts moving again. This seems to be some sort of feature to reduce memory usage. Currently I still haven't found a workaround.
