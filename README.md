# RMIProject
Small program using Java's RMI (Remote Methode Invocation) to write a server/client multiplayer version of the popular game "Asteroids".

## How to run
* Clone the repository
* Windows: 
  * `cd` into the repository and execute `runWindows.bat`.
  * Alternatively, start each step manually:
    * Compile: `javac *.java`
    * Registry: `start rmiregistry`
    * Server: `java -D"java.rmi.server.hostname=0.0.0.0" -D"java.rmi.server.codebase=file:///PFAD/ZUM/REPO/HIER/" AsteroidsServer`
    * Client(s):`java AsteroidsClient 0.0.0.0 username`
*  Linux:
  * `cd` into the repository, `chmod +x run` and `./run`
  * Alternatively, start each step manually:
    * Compile: `javac *.java`
    * Registry: `rmiregistry &`
    * Server: `java -Djava.rmi.server.hostname=0.0.0.0 -Djava.rmi.server.codebase=file:///PFAD/ZUM/REPO/HIER/ AsteroidsServer`
    * Client(s):`java AsteroidsClient 0.0.0.0 username` 

## How to play
To start the game, or reset after everyone has died, press `enter`.
Stay alive by dodging the asteroids or blowing them up with your lasers!
Use A/D to turn the ship, W to accelerate and J to give yourself a speed boost.
For each asteroid you destroy you will be awarded ```10/15/25/50``` points, based on its size (largest to smallest).
The winner is the one with the most points once every player dies.
