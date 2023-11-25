package antifraud.requests;

import jakarta.validation.constraints.NotNull;

public record IpRequest(@NotNull String ip) {
}
