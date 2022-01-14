package thkoeln.dungeon.eventconsumer.core;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class KafkaError {
    @Id
    @Getter
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Setter
    @Getter
    private String topic;
    @Setter
    @Getter
    private String message;
    @Setter
    @Getter
    private String payload;

    public KafkaError(String topic, String message, String payload){
        this.topic = topic;
        this.message = message;
        this.payload = payload;
    }
}
