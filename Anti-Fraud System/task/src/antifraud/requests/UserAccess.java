package antifraud.requests;

import antifraud.models.LockStatus;
import jakarta.validation.constraints.NotNull;

public record UserAccess(@NotNull String username, @NotNull LockStatus operation) {
}
