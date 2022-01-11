package thkoeln.dungeon.test

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Test(val name: String = "yeet") {
    @Id
    val id: UUID = UUID.randomUUID()
}
