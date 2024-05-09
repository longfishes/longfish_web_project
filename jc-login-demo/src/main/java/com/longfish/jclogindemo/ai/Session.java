package com.longfish.jclogindemo.ai;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Session {

    private final List<String> user;

    private final List<String> assistant;

    public Session() {
        user = new ArrayList<>();
        assistant = new ArrayList<>();
    }

    public void addMessageUser(String message) {
        user.add(message);
    }

    public void addMessageAssistant(String message) {
        assistant.add(message);
    }
}
