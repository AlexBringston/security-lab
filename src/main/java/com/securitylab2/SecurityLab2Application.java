package com.securitylab2;

import com.securitylab2.services.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SecurityLab2Application {


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SecurityLab2Application.class, args);

//        FileService fileService = (FileService) context.getBean("fileService");
//
//        ConsoleService consoleService = new ConsoleService(fileService);
//        consoleService.startConsole();
    }

}
