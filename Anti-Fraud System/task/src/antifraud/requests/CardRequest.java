package antifraud.requests;

import jakarta.validation.constraints.NotNull;

public record CardRequest(@NotNull String number) {
}
