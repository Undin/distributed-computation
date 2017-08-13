package com.warrior.solanteq.test.server;

import com.warrior.solanteq.test.configuration.AMQPConfig;
import com.warrior.solanteq.test.sender.AMQPSender;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Profile({"server"})
@Configuration
public class ServerConfig {

    @Bean
    public Server server(@Value("${server.task-size}") int taskSize,
                         RabbitTemplate template,
                         @Qualifier(AMQPConfig.TASK_QUEUE) Queue queue) {
        return new Server(taskSize, new AMQPSender(template, queue));
    }
}
