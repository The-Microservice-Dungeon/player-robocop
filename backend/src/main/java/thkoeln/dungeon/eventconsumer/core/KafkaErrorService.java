package thkoeln.dungeon.eventconsumer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaErrorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaErrorService.class);
    private final KafkaErrorRepository kafkaErrorRepository;

    @Autowired
    public KafkaErrorService(KafkaErrorRepository kafkaErrorRepository) {
        this.kafkaErrorRepository = kafkaErrorRepository;
    }

    public void newKafkaError(String topic, String consumerRecord, String exception) {
        KafkaError error = new KafkaError(topic, exception, consumerRecord);
        LOGGER.error("Error processing " + topic + " event." + "\nException: " + exception + "\nPayload: " + consumerRecord);
        this.kafkaErrorRepository.save(error);
    }
}
