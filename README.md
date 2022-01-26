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
`jdbc:h2:<ABSOLUTE/PATH/TO/DB-FILE>;AUTO_SERVER=TRUE`
5. Connect + Refresh
6. ???
7. Profit

## Architecture 

The rough system structure of this package looks like this:

![Domain Model](model/System-Structure.png)
