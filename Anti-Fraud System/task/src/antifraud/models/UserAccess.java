package antifraud.models;

import jakarta.validation.constraints.NotNull;

public record UserAccess(@NotNull String username, @NotNull LockStatus operation) {
}
