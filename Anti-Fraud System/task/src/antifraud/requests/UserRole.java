package antifraud.requests;

import jakarta.validation.constraints.NotNull;

public record UserRole(@NotNull String username, @NotNull String role) {
}
