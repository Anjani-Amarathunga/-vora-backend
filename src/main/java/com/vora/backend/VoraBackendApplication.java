package com.vora.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class VoraBackendApplication {

    public static void main(String[] args) {
        loadLocalEnvFile();
        SpringApplication.run(VoraBackendApplication.class, args);
    }

    private static void loadLocalEnvFile() {
        Path envFile = Path.of(".env.local");
        if (!Files.exists(envFile)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(envFile);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                    continue;
                }

                String[] parts = trimmed.split("=", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();

                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read .env.local", exception);
        }
    }
}


//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
//public class EcommerceApplication {
  //  public static void main(String[] args) {
    //    SpringApplication.run(EcommerceApplication.class, args);
    //}
//}
