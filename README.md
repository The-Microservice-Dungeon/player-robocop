# Robocop Player for _The Microservice Dungeon_

## Architecture 

The domain model is as such:

![Domain Model](model/Player-Domain-Model.png)

The packages are designed around the aggregates, according the basic DDD approach to software architecture. 
This leads to the following internal package dependency structure (without event and REST adapters):

![Internal Package Dependencies](model/Internal-Package-Dependencies.png)

Connection to the Apache Kafka is abstracted through the `eventconnector` package, connection to Game service (via REST)
through the `restadapter` package. (For internal package dependency see above - not depicted here for more clarity.)

![Architecture Overview](model/Adapter-Dependencies.png)
