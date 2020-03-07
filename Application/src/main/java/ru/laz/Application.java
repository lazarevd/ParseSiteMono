package ru.laz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;


@EnableScheduling
//@EnableJpaRepositories //redundant with jpa boot starter
@SpringBootApplication(scanBasePackages = "ru.laz")//for common module configuration
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.debug((new File("./").getAbsolutePath()));
        SpringApplication.run(Application.class, args);
    }

}