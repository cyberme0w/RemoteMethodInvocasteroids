javac *.java

start rmiregistry

start "" java -D'java.rmi.server.hostname=0.0.0.0' -D'java.rmi.server.codebase=%CD%' AsteroidsServer

sleep 1

java AsteroidsClient 0.0.0.0 someusername
