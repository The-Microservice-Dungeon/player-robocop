package thkoeln.dungeon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("thkoeln.dungeon.*")
public class DungeonPlayerMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(DungeonPlayerMainApplication.class, args);
    }

}
