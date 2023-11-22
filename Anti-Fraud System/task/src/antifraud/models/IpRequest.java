package antifraud.models;

import jakarta.validation.constraints.NotNull;

public record IpRequest(@NotNull String ip) {
}
