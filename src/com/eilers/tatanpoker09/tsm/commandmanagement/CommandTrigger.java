package com.eilers.tatanpoker09.tsm.commandmanagement;

public interface CommandTrigger{
    void call(String topic, String[] args);

    String buildCallback(String... args);
}