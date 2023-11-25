package antifraud.requests;

import antifraud.models.Region;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TransactionRequest(@NotNull Integer amount, @NotNull String ip, @NotNull String number,
                                 @NotNull Region region, LocalDateTime date) {
}
