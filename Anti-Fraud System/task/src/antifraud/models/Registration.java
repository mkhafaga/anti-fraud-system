package antifraud.models;


import jakarta.validation.constraints.NotNull;

public record Registration(@NotNull String name, @NotNull String username, @NotNull String password) {
}
