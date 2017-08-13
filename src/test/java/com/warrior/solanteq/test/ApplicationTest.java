package com.warrior.solanteq.test;

import com.warrior.solanteq.test.server.Server;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

@ActiveProfiles(profiles = {"test", "server", "worker"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    private static final int[][] DATA = {
            {1, 2},
            {3, 4},
            {5, 6},
            {7, 8},
            {9, 10},
            {11, 12},
            {13, 14},
            {15, 16}
    };

    @Autowired
    private Server server;

    @Test
    public void contextLoads() {}

    @Test
    public void testComputation() throws Exception {
        int result = server.compute("x - y", "x + y", toDataStream(DATA));
        Assertions.assertThat(result).isEqualTo(-8);
    }

    @Test(expected = IllegalStateException.class)
    public void testTooSmallInput() throws Exception {
        server.compute("x - y", "x + y", toDataStream(new int[][]{{1, 2}}));
    }

    private static InputStream toDataStream(int[][] data) {
        String stringData = Arrays.stream(data)
                .map(arr -> arr[0] + " " + arr[1])
                .collect(Collectors.joining("\n"));
        return new ByteArrayInputStream(stringData.getBytes());
    }
}
