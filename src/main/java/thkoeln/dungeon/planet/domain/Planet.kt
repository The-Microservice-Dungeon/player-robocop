package thkoeln.dungeon.planet.domain

import lombok.*
import org.apache.commons.text.WordUtils
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import javax.persistence.*

@Entity
@Getter
class Planet {
    @Id
    val id = UUID.randomUUID()

    @Setter
    var name: String? = null

    @Setter
    @Getter(AccessLevel.NONE)
    val isSpaceStation = java.lang.Boolean.FALSE

    @OneToOne(cascade = [CascadeType.MERGE])
    @Setter(AccessLevel.PROTECTED)
    val northNeighbour: Planet? = null

    @OneToOne(cascade = [CascadeType.MERGE])
    @Setter(AccessLevel.PROTECTED)
    val eastNeighbour: Planet? = null

    @OneToOne(cascade = [CascadeType.MERGE])
    @Setter(AccessLevel.PROTECTED)
    val southNeighbour: Planet? = null

    @OneToOne(cascade = [CascadeType.MERGE])
    @Setter(AccessLevel.PROTECTED)
    val westNeighbour: Planet? = null

    @Transient
    private val logger = LoggerFactory.getLogger(Planet::class.java)

    /**
     * A neighbour relationship is always set on BOTH sides.
     * @param otherPlanet
     * @param direction
     */
    fun defineNeighbour(otherPlanet: Planet?, direction: CompassDirection) {
        if (otherPlanet == null) throw PlanetException("Cannot establish neighbouring relationship with null planet!")
        try {
            val otherGetter = neighbouringGetter(direction.oppositeDirection)
            val setter = neighbouringSetter(direction)
            setter.invoke(this, otherPlanet)
            val remoteNeighbour = otherGetter.invoke(otherPlanet) as Planet
            if (this != remoteNeighbour) {
                val otherSetter = neighbouringSetter(direction.oppositeDirection)
                otherSetter.invoke(otherPlanet, this)
            }
        } catch (e: IllegalAccessException) {
            throw PlanetException("Something went wrong that should not have happened ..." + e.stackTrace)
        } catch (e: InvocationTargetException) {
            throw PlanetException("Something went wrong that should not have happened ..." + e.stackTrace)
        } catch (e: NoSuchMethodException) {
            throw PlanetException("Something went wrong that should not have happened ..." + e.stackTrace)
        }
        logger.info("Established neighbouring relationship between planet '$this' and '$otherPlanet'.")
    }

    @Throws(NoSuchMethodException::class)
    fun neighbouringGetter(direction: CompassDirection?): Method {
        val name = "get" + WordUtils.capitalize(direction.toString()) + "Neighbour"
        return this.javaClass.getDeclaredMethod(name)
    }

    @Throws(NoSuchMethodException::class)
    fun neighbouringSetter(direction: CompassDirection?): Method {
        val name = "set" + WordUtils.capitalize(direction.toString()) + "Neighbour"
        return this.javaClass.getDeclaredMethod(name, *arrayOf<Class<*>>(this.javaClass))
    }

    fun allNeighbours(): List<Planet> {
        val allNeighbours: MutableList<Planet> = ArrayList()
        if (this.northNeighbour != null) allNeighbours.add(this.northNeighbour)
        if (this.westNeighbour != null) allNeighbours.add(this.westNeighbour)
        if (this.eastNeighbour != null) allNeighbours.add(this.eastNeighbour)
        if (this.southNeighbour != null) allNeighbours.add(this.southNeighbour)
        return allNeighbours
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Planet) return false
        return id == o.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return this.name + " (" + this.id + ")"
    }
}