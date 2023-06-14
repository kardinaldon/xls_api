package com.natlex.test_app.component;

import com.natlex.test_app.model.exception.FileStorageException;
import com.natlex.test_app.service.impl.AppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AppRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AppRunner.class);
    @Value("${values.input_folder}")
    private Path inputFolder;
    @Value("${values.output_folder}")
    private Path outputFolder;
    @Autowired
    private AppUserService appUserService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        try {
            Files.createDirectories(outputFolder);
            Files.createDirectories(inputFolder);
        }
        catch (Exception ex) {
            LOG.error("Could not create upload and download dir!");
            throw new FileStorageException("Could not create upload and download dir!");
        }
    }

}

