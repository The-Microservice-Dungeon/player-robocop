package thkoeln.dungeon

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableAutoConfiguration
@EntityScan("thkoeln.dungeon.*")
@ComponentScan("thkoeln.dungeon.*")
class DungeonPlayerConfiguration {
    @Autowired
    private val restTemplateBuilder: RestTemplateBuilder? = null

    @Bean
    fun restTemplate(): RestTemplate {
        return restTemplateBuilder!!.build()
    }
}