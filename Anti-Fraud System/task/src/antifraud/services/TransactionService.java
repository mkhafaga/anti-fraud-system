package antifraud.services;

import antifraud.models.TransactionStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public TransactionStatus checkTransaction(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount <= 200) {
            return TransactionStatus.ALLOWED;
        } else if (amount > 200 && amount <= 1500) {
            return TransactionStatus.MANUAL_PROCESSING;
        } else {
            return TransactionStatus.PROHIBITED;
        }
    }
}
