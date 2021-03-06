package thkoeln.dungeon.endpoints.actuator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Very pragmatic approach to access the service logs remotely.
 * Just an additional actuator endpoint which parses the log file configured by spring
 * boot and returns its content in plain text.
 * <p>
 * Bad Idea for production usage, but should do its job for us ¯\_(ツ)_/¯
 * <p>
 * TODO: Use a RollingFileAppender w/ .zip archives that can be retrieved from here. But this
 *       is only necessary if the log file will get big. The effort might be not worthy.
 */
@Component
@Endpoint(id = "logs")
@Slf4j
public record LogsEndpoint(String logFilePath) {
    @Autowired
    public LogsEndpoint(
            @Value("${logging.file.path}/spring.log")
                    String logFilePath) {
        this.logFilePath = logFilePath;
    }

    // GET /actuator/logs
    @ReadOperation(produces = MediaType.TEXT_PLAIN_VALUE)
    public String logs() {
        Path path = Paths.get(logFilePath);

        try {
            Stream<String> lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));
            lines.close();

            return data;
        } catch (IOException e) {
            log.error("Could not read log file", e);
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}