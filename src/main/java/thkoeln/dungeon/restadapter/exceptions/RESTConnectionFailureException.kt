package thkoeln.dungeon.restadapter.exceptions

import thkoeln.dungeon.DungeonPlayerException

/**
 * The connection to GameService could not be established (network failure or GameService down). The player
 * business logic needs to deal with this and try again later.
 */
class RESTConnectionFailureException(endPoint: String, message: String?) : DungeonPlayerException(
    """Unexpected error in communication with GameService calling $endPoint endpoint. Message: $message
Need to retry later."""
)