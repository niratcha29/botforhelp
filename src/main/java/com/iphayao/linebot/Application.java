package com.iphayao.linebot;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.google.common.io.Files;

import io.swagger.models.Path;

public class Application extends SpringBootServletInitializer {
	public static Path downloadedContentDir;
    public static void main(String[] args) throws IOException {
//        downloadedContentDir = Files.createTempDirectory("line-bot");
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
