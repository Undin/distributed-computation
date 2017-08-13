package com.warrior.solanteq.test.worker;

import com.warrior.solanteq.test.configuration.AMQPConfig;
import com.warrior.solanteq.test.sender.AMQPSender;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Profile({"worker"})
@Configuration
public class WorkerConfig {

    @Bean
    public Worker worker(RabbitTemplate template, @Qualifier(AMQPConfig.RESULT_QUEUE) Queue queue) {
        return new Worker(new AMQPSender(template, queue));
    }
}
