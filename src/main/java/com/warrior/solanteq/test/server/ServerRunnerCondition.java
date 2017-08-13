package com.warrior.solanteq.test.server;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ServerRunnerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().acceptsProfiles("server")
                && !context.getEnvironment().acceptsProfiles("test");
    }
}
