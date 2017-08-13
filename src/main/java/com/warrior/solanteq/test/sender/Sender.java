package com.warrior.solanteq.test.sender;

import org.jetbrains.annotations.NotNull;

public interface Sender {
    void sendMessage(@NotNull Object message) throws Exception;
}
