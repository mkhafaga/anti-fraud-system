package antifraud.requests;

import antifraud.models.TransactionStatus;
import jakarta.validation.constraints.NotNull;

public record TransactionFeedback(@NotNull long transactionId, @NotNull TransactionStatus feedback) {
}
