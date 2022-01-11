package thkoeln.dungeon.restadapter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.*
import java.util.*

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
class PlayerRegistryDto {
    var name: String? = null
    var email: String? = null
    var bearerToken: UUID? = null
    fun clone(): PlayerRegistryDto {
        val myClone = PlayerRegistryDto()
        myClone.bearerToken = bearerToken
        myClone.name = name
        myClone.email = email
        return myClone
    }
}