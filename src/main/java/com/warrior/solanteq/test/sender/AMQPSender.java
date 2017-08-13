package com.warrior.solanteq.test.sender;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class AMQPSender implements Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPSender.class);

    @NotNull
    private final RabbitTemplate template;

    @NotNull
    private final Queue queue;

    public AMQPSender(@NotNull RabbitTemplate template, @NotNull Queue queue) {
        this.template = template;
        this.queue = queue;
    }

    @Override
    public void sendMessage(@NotNull Object message) throws AmqpException {
        LOGGER.debug("Send message: {}", message);
        template.convertAndSend(queue.getName(), message);
    }
}
