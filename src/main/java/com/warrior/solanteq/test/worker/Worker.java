package com.warrior.solanteq.test.worker;

import com.warrior.solanteq.test.message.ResultMessage;
import com.warrior.solanteq.test.message.TaskMessage;
import com.warrior.solanteq.test.script.GroovyScriptLoader;
import com.warrior.solanteq.test.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

public class Worker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    private final GroovyScriptLoader groovyScriptLoader = new GroovyScriptLoader();
    private final Sender sender;

    public Worker(@NotNull Sender sender) {
        this.sender = sender;
    }

    @RabbitListener(queues = "${rabbitmq.task-queue}")
    public void compute(@NotNull TaskMessage task) {
        LOGGER.debug("Task message: {}", task);

        IntBinaryOperator f1 = groovyScriptLoader.createIntBinaryFunction(task.f1);
        IntBinaryOperator f2 = groovyScriptLoader.createIntBinaryFunction(task.f2);

        List<Integer> data = task.data;

        int result;
        if (data.size() == 2) {
            result = f1.applyAsInt(data.get(0), data.get(1));
        } else {
            result = IntStream.range(0, data.size() / 2)
                    .map(i -> f1.applyAsInt(data.get(2 * i), data.get(2 * i + 1)))
                    .reduce(f2)
                    // It's safe because we check that data.size > 2
                    .getAsInt();
        }

        ResultMessage resultMessage = ResultMessage.success(result);
        try {
            sender.sendMessage(resultMessage);
        } catch (Exception e) {
            LOGGER.error("Failed to send message", e);
        }
    }
}
