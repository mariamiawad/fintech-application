package com.axis.fintech.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FintechMenuRunner implements CommandLineRunner {
    private final FintechApp fintechApp;

    public FintechMenuRunner(FintechApp fintechApp) {
        this.fintechApp = fintechApp;
    }

    @Override
    public void run(String... args) {
        fintechApp.start();
    }
}
