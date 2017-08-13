package com.warrior.solanteq.test.server;

import com.warrior.solanteq.test.message.ResultMessage;
import com.warrior.solanteq.test.message.TaskMessage;
import com.warrior.solanteq.test.script.GroovyScriptLoader;
import com.warrior.solanteq.test.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.function.IntBinaryOperator;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final GroovyScriptLoader groovyScriptLoader = new GroovyScriptLoader();
    private final int taskSize;
    private final Sender sender;

    private final Object lock = new Object();

    private IntBinaryOperator f1;
    private IntBinaryOperator f2;

    private Phaser phaser;
    private int result;

    public Server(int taskSize, @NotNull Sender sender) {
        this.taskSize = taskSize;
        this.sender = sender;
    }

    @RabbitListener(queues = "${rabbitmq.result-queue}")
    public void receiveResult(@NotNull ResultMessage resultMessage) throws InterruptedException {
        LOGGER.debug("Result message: {}", resultMessage);

        if (resultMessage.value != null) {
            synchronized (lock) {
                result = f2.applyAsInt(result, resultMessage.value);
            }
        } else {
            LOGGER.error(resultMessage.errorMessage);
            throw new RuntimeException(String.format("Computation failed due to worker error: '%s'", resultMessage.errorMessage));
        }
        phaser.arrive();
    }

    public int compute(@NotNull String scriptF1, @NotNull String scriptF2, @NotNull InputStream dataStream) throws Exception {
        LOGGER.info("start computation");

        f1 = groovyScriptLoader.createIntBinaryFunction(scriptF1);
        f2 = groovyScriptLoader.createIntBinaryFunction(scriptF2);

        phaser = new Phaser(1);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream))) {
            String firstLine = reader.readLine();
            String secondLine = reader.readLine();

            result = computeInitialValue(firstLine, secondLine);

            List<Integer> data = new ArrayList<>(2 * taskSize);
            reader.lines().forEach(line -> {
                int[] numbers = convertInputLine(line);
                data.add(numbers[0]);
                data.add(numbers[1]);
                if (data.size() == 2 * taskSize) {
                    sendTask(scriptF1, scriptF2, data);
                    data.clear();
                }
            });
            if (!data.isEmpty()) {
                sendTask(scriptF1, scriptF2, data);
            }
        }

        phaser.arriveAndAwaitAdvance();
        return result;
    }

    private void sendTask(@NotNull String scriptF1, @NotNull String scriptF2, @NotNull List<Integer> data) {
        TaskMessage task = new TaskMessage(scriptF1, scriptF2, data);
        try {
            sender.sendMessage(task);
            phaser.register();
        } catch (Exception e) {
            LOGGER.error("Failed to send message", e);
        }
    }

    private int computeInitialValue(@Nullable String firstLine, @Nullable String secondLine) {
        if (firstLine == null || secondLine == null) {
            throw new IllegalStateException("Computation failed because input data file has less than 2 lines of data");
        }
        int[] firstNumbers = convertInputLine(firstLine);
        int[] secondNumber = convertInputLine(secondLine);

        int firstResult = f1.applyAsInt(firstNumbers[0], firstNumbers[1]);
        int secondResult = f1.applyAsInt(secondNumber[0], secondNumber[1]);
        return f2.applyAsInt(firstResult, secondResult);
    }

    @NotNull
    private int[] convertInputLine(@NotNull String line) {
        String[] lineParts = line.split(" ");
        assert lineParts.length == 2;

        int[] result = new int[2];
        result[0] = Integer.parseInt(lineParts[0]);
        result[1] = Integer.parseInt(lineParts[1]);
        return result;
    }
}
