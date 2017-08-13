package com.warrior.solanteq.test.server;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;
import java.util.List;

public class ServerApplicationRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerApplicationRunner.class);

    @Autowired
    private ConfigurableApplicationContext ctx;

    @Autowired
    private Server server;

    @Override
    public void run(@NotNull ApplicationArguments args) throws Exception {
        String scriptF1Path = getValue(args, "f1");
        String scriptF2Path = getValue(args, "f2");
        String dataPath =  getValue(args, "data");

        String scriptF1 = null;
        String scriptF2 = null;
        try {
            scriptF1 = readFile(scriptF1Path);
            scriptF2 = readFile(scriptF2Path);
        } catch (IOException e) {
            LOGGER.error("Failed to read scripts", e);
        }

        if (scriptF1 != null && scriptF2 != null) {
            try (InputStream dataStream = new FileInputStream(dataPath)) {
                int result = server.compute(scriptF1, scriptF2, dataStream);
                LOGGER.info("Result: " + result);
            } catch (Exception e) {
                LOGGER.error("Failed to compute result", e);
            }
        }

        ctx.close();
    }

    @NotNull
    private String readFile(@NotNull String path) throws IOException {
        char[] buffer = new char[4 * 1024];
        StringWriter writer = new StringWriter();
        int n;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }
        return writer.toString();
    }

    @NotNull
    private String getValue(@NotNull ApplicationArguments args, @NotNull String key) {
        List<String> values = args.getOptionValues(key);
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(String.format("Command line args must contain '%s' key", key));
        }
        return values.get(0);
    }
}
