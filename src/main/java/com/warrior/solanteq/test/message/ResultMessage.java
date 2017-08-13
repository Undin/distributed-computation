package com.warrior.solanteq.test.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResultMessage {

    @Nullable
    @JsonProperty("value")
    public final Integer value;

    @Nullable
    @JsonProperty("message")
    public final String errorMessage;

    @JsonCreator
    private ResultMessage(@Nullable @JsonProperty("value") Integer value, @Nullable @JsonProperty("message") String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ResultMessage{" +
                "value=" + value +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    @NotNull
    public static ResultMessage success(int value) {
        return new ResultMessage(value, null);
    }

    @NotNull
    public static ResultMessage error(@NotNull String errorMessage) {
        return new ResultMessage(null, errorMessage);
    }
}
