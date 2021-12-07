# Generic Player

This repo contains a generic Dungeon player. It can be used  
1. for **integration testing** the interaction between Player, Game, Robot, Map, Trading, and GameLog service, or 
2. as a **starting point** to build an own player.

In order to fulfill goal (1), this service can instantiate **more than one player**. If some Dungeon team uses this
to build their own player, of course they should instantiate **only one player**. 

## Profiles and Game Connector Components

The service maintains several Spring profiles, in order to serve the above goals: 
* **mock** - used for unit-testing the basic player logic. No Kafka, no REST calls. 
* **play** - (fixme)

Connection to the Apache Kafka and the Game service (via REST) is abstracted through the `gameconnector` package. 
In `mock` profile, all event listeners and REST calls are fully mocked with random data. The `play` profile can 
be used locally or in production environment. If used locally, all dungeon services need to be locally running
using the Docker Compose script (fixme - link), plus a local Kafka. 

## At startup

At startup, the service instantiates one or several players, depending on the chosen profile. 

## Some basic strategy

This is the (very crude ...) strategy that the 

1. 
