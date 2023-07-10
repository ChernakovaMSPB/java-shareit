package ru.practicum.shareit.handler;

import lombok.Getter;

@Getter
public class ErrorMessage {
    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }
}
