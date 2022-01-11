package thkoeln.dungeon.player.domain

enum class PlayerMode {
    SINGLE, MULTI;

    val isSingle: Boolean
        get() = this == SINGLE
}