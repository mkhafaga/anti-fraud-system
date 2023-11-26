package antifraud.services;

import antifraud.database.CardLimitRepository;
import antifraud.models.CardLimit;
import antifraud.models.TransactionLog;
import antifraud.models.TransactionStatus;
import org.springframework.stereotype.Service;

@Service
public class CardLimitService {
    private static final int MAX_ALLOWED = 200;
    private static final int MAX_MANUAL = 1500;
    private final CardLimitRepository cardLimitRepository;

    public CardLimitService(CardLimitRepository cardLimitRepository) {
        this.cardLimitRepository = cardLimitRepository;
    }

    public CardLimit getCardLimit(String cardNumber) {
        return cardLimitRepository.findCardLimitByNumber(cardNumber)
                .orElse(new CardLimit(cardNumber, MAX_ALLOWED, MAX_MANUAL));
    }

    public void updateCardLimit(TransactionLog transactionLog, TransactionStatus transactionFeedback) {
        CardLimit cardLimit = cardLimitRepository.findCardLimitByNumber(transactionLog.getNumber()).
                orElse(new CardLimit(transactionLog.getNumber(), MAX_ALLOWED, MAX_MANUAL));
        TransactionStatus transactionResult = TransactionStatus.valueOf(transactionLog.getResult());
        if (transactionResult == TransactionStatus.ALLOWED) {
            if (transactionFeedback == TransactionStatus.MANUAL_PROCESSING) {
                int maxAllowed = (int) Math.ceil(0.8 * cardLimit.getMaxAllowed() + 0.2 * transactionLog.getAmount());
                cardLimit.setMaxAllowed(maxAllowed);
            } else {
                int maxAllowed = (int) Math.ceil(0.8 * cardLimit.getMaxAllowed() - 0.2 * transactionLog.getAmount());
                int maxManual = (int) Math.ceil(0.8 * cardLimit.getManualMax() - 0.2 * transactionLog.getAmount());
                cardLimit.setMaxAllowed(maxAllowed);
                cardLimit.setManualMax(maxManual);
            }
        } else if (transactionResult == TransactionStatus.MANUAL_PROCESSING) {
            if (transactionFeedback == TransactionStatus.ALLOWED) {
                int maxAllowed = (int) Math.ceil(0.8 * cardLimit.getMaxAllowed() + 0.2 * transactionLog.getAmount());
                cardLimit.setMaxAllowed(maxAllowed);
            } else {
                int maxManual = (int) Math.ceil(0.8 * cardLimit.getManualMax() - 0.2 * transactionLog.getAmount());
                cardLimit.setManualMax(maxManual);
            }
        } else {
            if (transactionFeedback == TransactionStatus.ALLOWED) {
                int maxAllowed = (int) Math.ceil(0.8 * cardLimit.getMaxAllowed() + 0.2 * transactionLog.getAmount());
                int maxManual = (int) Math.ceil(0.8 * cardLimit.getManualMax() + 0.2 * transactionLog.getAmount());
                cardLimit.setMaxAllowed(maxAllowed);
                cardLimit.setManualMax(maxManual);
            } else {
                int maxManual = (int) Math.ceil(0.8 * cardLimit.getManualMax() + 0.2 * transactionLog.getAmount());
                cardLimit.setManualMax(maxManual);
            }
        }
        cardLimitRepository.save(cardLimit);
    }
}
