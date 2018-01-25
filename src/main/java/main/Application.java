package main;

import main.dao.*;
import main.models.*;
import main.controllers.*;
import main.storage.StorageProperties;
import main.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService, DocumentDao documentDao, UserDao userDao, ShareDao shareDao) {
        return (args) -> {
            /* Uncomment these to wipe database and/or filesystem*/
            //documentDao.deleteAll(); // document database
            //storageService.deleteAll(); // files in filesystem
            //userDao.deleteAll(); // user database
            //shareDao.deleteAll(); // all Shares

            storageService.init(); // don't comment out, initialises filesystem branches
        };
    }
}