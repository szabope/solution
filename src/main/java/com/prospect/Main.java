package com.prospect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, URISyntaxException {
        Properties properties = new Properties();
        InputStream propertiesIs = Main.class.getResourceAsStream("/application.properties");
        properties.load(propertiesIs);
        int numberOfThreads = Integer.parseInt(properties.getProperty("num_threads"));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numberOfThreads);
        URL domainConfigDir = Main.class.getResource("/domains");
        assert domainConfigDir != null;
        try(Stream<Path> files = Files.list(Path.of(domainConfigDir.toURI()))) {
            files.forEach(path -> {
                Properties props = new Properties();
                try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
                    props.load(is);
                    DomainConfig domainConfig = new DomainConfig(props);
                    scheduler.scheduleAtFixedRate(domainConfig::check, 0, domainConfig.getInterval(), TimeUnit.SECONDS);
                } catch (IOException e) {
                    LOGGER.error("Can read domain config {}.", path);
                    throw new IllegalArgumentException(e);
                }
            });
        }
    }
}
