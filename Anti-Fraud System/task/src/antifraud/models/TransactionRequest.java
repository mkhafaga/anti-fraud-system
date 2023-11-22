package antifraud.models;

import jakarta.validation.constraints.NotNull;

public record TransactionRequest(@NotNull Integer amount, @NotNull String ip, @NotNull String number) {
}
