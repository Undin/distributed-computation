package com.warrior.solanteq.test.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TaskMessage {

    @NotNull
    @JsonProperty("f1")
    public final String f1;

    @NotNull
    @JsonProperty("f2")
    public final String f2;

    @NotNull
    @JsonProperty("data")
    public final List<Integer> data;

    @JsonCreator
    public TaskMessage(@NotNull @JsonProperty("f1") String f1,
                       @NotNull @JsonProperty("f2") String f2,
                       @NotNull @JsonProperty("data") List<Integer> data) {
        this.f1 = f1;
        this.f2 = f2;
        assert data.size() > 0 && data.size() % 2 == 0;
        this.data = data;
    }

    @Override
    public String toString() {
        return "TaskMessage{" +
                "f1='" + f1 + '\'' +
                ", f2='" + f2 + '\'' +
                ", data=" + data +
                '}';
    }
}
