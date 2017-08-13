package com.warrior.solanteq.test.script;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.function.IntBinaryOperator;

public class GroovyScriptLoaderTest {

    private GroovyScriptLoader scriptLoader = new GroovyScriptLoader();

    @Test
    public void testEvalSampleScript() {
        test(10, 15, "2 * x - y", (left, right) -> 2 * left - right);
    }

    @Test
    public void testEvalComplexScript() {
        String script = "fib(x) - fib(y)\n" +
                "\n" +
                "def fib(int x) {\n" +
                "    if (0 == x || 1 == x) x else fib(x - 1) + fib(x - 2)\n" +
                "}";
        test(7, 4, script, (left, right) -> fib(left) - fib(right));
    }

    private void test(int x, int y, String script, IntBinaryOperator testFunction) {
        IntBinaryOperator scriptFunction = scriptLoader.createIntBinaryFunction(script);
        int result = scriptFunction.applyAsInt(x, y);
        int expected = testFunction.applyAsInt(x, y);
        Assertions.assertThat(result).isEqualTo(expected);
    }

    private int fib(int x) {
        if (x == 0 || x == 1) return x;
        return fib(x - 1) + fib(x - 2);
    }
}
