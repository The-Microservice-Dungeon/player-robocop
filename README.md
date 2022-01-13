# Robocop Player for _The Microservice Dungeon_

## Authors
| Name             | Github          |
|------------------|-----------------|
| Jannik Alexander | @InstantRegrets |
| Oliver Wichmann  | @OliverW21      |
| Thomas Raser     | @ThmsRa         |


## Installation
1. Import `player-robocop` Maven Package
2. Let Intellij figure out everything and install the packages
3. Choose `RunAll` run configuration. This will start spring and the Vue frontend
- frontend will be available on [http://localhost:8103/](http://localhost:8103/#/)
- All other routes will be passed to Spring

### Database Inspection
To setup an IntelliJ database connection:
1. Select H2 as Datasource
2. Choose "Remote" as the connection type
3. Set user to robocop and password to password
4. modify the connection URL to look like this: 
```
jdbc:h2:<ABSOLUTE/PATH/TO/DB-FILE>;AUTO_SERVER=TRUE
```
5. Connect + Refresh
6. ???
7. Profit

## Architecture 

The domain model is as such:

![Domain Model](model/Player-Domain-Model.png)

The packages are designed around the aggregates, according the basic DDD approach to software architecture. 
This leads to the following internal package dependency structure (without event and REST adapters):

![Internal Package Dependencies](model/Internal-Package-Dependencies.png)

Connection to the Apache Kafka is abstracted through the `eventconnector` package, connection to Game service (via REST)
through the `restadapter` package. (For internal package dependency see above - not depicted here for more clarity.)

![Architecture Overview](model/Adapter-Dependencies.png)
