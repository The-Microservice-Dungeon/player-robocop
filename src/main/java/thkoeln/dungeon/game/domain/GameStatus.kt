package thkoeln.dungeon.game.domain

enum class GameStatus {
    CREATED, IN_PREPARATION, GAME_RUNNING, GAME_FINISHED, ORPHANED // this is the state a game takes when the GameService doesn't list it anymore
}