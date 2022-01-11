package thkoeln.dungeon.planet.domain

import org.springframework.data.repository.CrudRepository
import java.util.*

interface PlanetRepository : CrudRepository<Planet?, UUID?> {
    override fun findAll(): List<Planet?>
    fun findByName(name: String?): Optional<Planet?>?
}