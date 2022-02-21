package com.scd.filecheckerpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileCheckerPcApplication {

  public static void main(String[] args) {
    new FileChecker();
    SpringApplication.run(FileCheckerPcApplication.class, args);
  }

}

