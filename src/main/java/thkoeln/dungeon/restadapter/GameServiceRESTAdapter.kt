package thkoeln.dungeon.restadapter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException
import thkoeln.dungeon.restadapter.exceptions.RESTRequestDeniedException
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException
import java.util.*

@Component
class GameServiceRESTAdapter @Autowired constructor(private val restTemplate: RestTemplate) {
    private val logger = LoggerFactory.getLogger(GameServiceRESTAdapter::class.java)

    @Value("\${GAME_SERVICE}")
    private val gameServiceUrlString: String? = null

    @Throws(UnexpectedRESTException::class, RESTConnectionFailureException::class)
    fun fetchCurrentGameState(): Array<GameDto?> {
        var gameDtos: Array<GameDto?>? = arrayOfNulls(0)
        val urlString = "$gameServiceUrlString/games"
        try {
            gameDtos = restTemplate.getForObject(urlString, Array<GameDto?>::class.java)
            if (gameDtos == null) throw UnexpectedRESTException("Received a null GameDto array - wtf ...?")
            logger.info("Got " + gameDtos.size + " game(s) via REST ...")
            val iterator = Arrays.stream(gameDtos).iterator()
            while (iterator.hasNext()) {
                logger.info("... " + iterator.next())
            }
        } catch (e: RestClientException) {
            throw RESTConnectionFailureException(urlString, e.message)
        }
        return gameDtos
    }

    @Throws(UnexpectedRESTException::class, RESTConnectionFailureException::class, RESTRequestDeniedException::class)
    fun getBearerTokenForPlayer(playerRegistryDto: PlayerRegistryDto): PlayerRegistryDto? {
        var returnedPlayerRegistryDto: PlayerRegistryDto? = null
        val urlString = "$gameServiceUrlString/players"
        try {
            val objectMapper = ObjectMapper()
            val json = objectMapper.writeValueAsString(playerRegistryDto)
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val request = HttpEntity(json, headers)
            returnedPlayerRegistryDto = restTemplate.postForObject(urlString, request, PlayerRegistryDto::class.java)
        } catch (e: JsonProcessingException) {
            throw UnexpectedRESTException(
                "Unexpected error converting playerRegistryDto to JSON: $playerRegistryDto"
            )
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatus.NOT_ACCEPTABLE) {
                // this is a business logic problem - so let the application service handle this
                throw RESTRequestDeniedException("Player $playerRegistryDto already registered")
            } else {
                throw RESTConnectionFailureException(urlString, "Status code " + e.statusCode)
            }
        } catch (e: RestClientException) {
            throw RESTConnectionFailureException("/players", e.message)
        }
        logger.info("Registered player via REST, got bearer token: " + returnedPlayerRegistryDto?.bearerToken)
        return returnedPlayerRegistryDto
    }

    /**
     * Register a specific player for a specific game via call to GameService endpoint.
     * Caveat: GameService returns somewhat weird error codes (non-standard).
     * @param gameId of the game
     * @param bearerToken of the player
     * @return true if successful
     */
    @Throws(RESTConnectionFailureException::class, RESTRequestDeniedException::class)
    fun registerPlayerForGame(gameId: UUID, bearerToken: UUID): Boolean {
        val urlString = "$gameServiceUrlString/games/$gameId/players/$bearerToken"
        return try {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val request = HttpEntity<String>(headers)
            restTemplate.put(urlString, request)
            true
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatus.NOT_ACCEPTABLE) {
                // this is a business logic problem - so let the application service handle this
                throw RESTRequestDeniedException(
                    "Player with bearer token " + bearerToken +
                            " already registered in game with id " + gameId
                )
            } else if (e.statusCode == HttpStatus.BAD_REQUEST) {
                throw RESTRequestDeniedException(
                    "For player with bearer token " + bearerToken +
                            " and game with id " + gameId + " the player registration went wrong; original error msg: "
                            + e.message
                )
            } else {
                throw RESTConnectionFailureException(urlString, "Status code " + e.statusCode)
            }
        } catch (e: RestClientException) {
            throw RESTConnectionFailureException(urlString, e.message)
        }
    }
}