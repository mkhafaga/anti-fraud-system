package antifraud.models;

import jakarta.validation.constraints.NotNull;

public record CardRequest(@NotNull String number) {
}
