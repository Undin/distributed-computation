package com.warrior.solanteq.test.script;

import groovy.lang.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.IntBinaryOperator;

public class GroovyScriptLoader {

    private final GroovyClassLoader groovyClassLoader = new GroovyClassLoader(getClass().getClassLoader());

    @NotNull
    public IntBinaryOperator createIntBinaryFunction(@NotNull String scriptText) {
        GroovyCodeSource codeSource = new GroovyCodeSource(scriptText,
                groovyClassLoader.generateScriptName(), GroovyShell.DEFAULT_CODE_BASE);
        codeSource.setCachable(true);
        Class aClass = groovyClassLoader.parseClass(codeSource);
        return (left, right) -> {
            HashMap<String, Integer> params = new HashMap<>();
            params.put("x", left);
            params.put("y", right);
            Binding binding = new Binding(params);
            Script script = InvokerHelper.createScript(aClass, binding);
            return (int) script.run();
        };
    }
}
