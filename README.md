# RMIProject
This program was one of the final projects for my university course. It uses Java's RMI (Remote Method Invocation) to access commands/objects stashed away in the RMI Server, while the RMI Client handles the GUI locally.

While the program itself is rather simple, it served its purpose and showed some of the capabilities of RMI.

I might improve the program in the future (or not).

# How does it work?

## RMI Remote Methods Interface
This interface serves as a reference for the local program. It sums up the available commands on the RMI Server that the client might access, and as such must always be up to date with the server.

## Implementation Class (stub)
This class is the so called "stub". The stub is used by the client as if it were an object representing the server. By using the stub just like one would use any other object, the stub speaks and listens to the server in order to give commands / retrieve answers.

## RMI Server

## RMI Client
The client handles mostly GUI related things, but it also handles the timer logic.

