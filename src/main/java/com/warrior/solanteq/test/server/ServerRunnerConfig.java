package com.warrior.solanteq.test.server;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;

@Configuration
public class ServerRunnerConfig {

    @Conditional(ServerRunnerCondition.class)
    @Bean
    public ApplicationRunner serverRunner() {
        return new ServerApplicationRunner();
    }
}
