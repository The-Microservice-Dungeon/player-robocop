package thkoeln.dungeon.planet.domain

enum class CompassDirection {
    north, east, south, west;

    val oppositeDirection: CompassDirection?
        get() {
            return when (this) {
                north -> south
                east -> west
                south -> north
                west -> east
            }
            return null
        }
}