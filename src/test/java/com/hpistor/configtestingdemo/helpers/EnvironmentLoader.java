package com.hpistor.configtestingdemo.helpers;

import com.hpistor.configtestingdemo.ConfigTestingDemoApplication;
import org.assertj.core.util.Arrays;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogs;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.*;

import java.util.List;

public class EnvironmentLoader {

    public static AbstractEnvironment loadForProfiles(List<String> profiles) {
        return loadForProfiles(profiles.toArray(new String[0]));
    }

    public static AbstractEnvironment loadForProfiles(String... profiles) {
        ConfigDataEnvironmentPostProcessor configPostProcessor =
                new ConfigDataEnvironmentPostProcessor(new DeferredLogs(), new DefaultBootstrapContext());
        StaticApplicationContext staticApplicationContext = new StaticApplicationContext();

        staticApplicationContext.setResourceLoader(new ResourceLoader() {
            @Override
            public Resource getResource(String location) {
                // Strips off the file: prefix from resource location
                String fileName = location.split(":")[1];
                return new FileSystemResource("./src/main/resources/" + fileName);
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }
        });

        SpringApplication application = new SpringApplication(staticApplicationContext, ConfigTestingDemoApplication.class);

        // Setting the active profiles
        application.setAdditionalProfiles(profiles);

        StandardEnvironment environment = new StandardEnvironment();
        configPostProcessor.postProcessEnvironment(environment, application);

        return environment;
    }

}
