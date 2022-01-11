package thkoeln.dungeon

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
object DungeonPlayerMainApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        SpringApplication.run(DungeonPlayerMainApplication::class.java, *args)
    }
}