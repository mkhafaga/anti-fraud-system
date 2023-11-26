package antifraud.services;

import antifraud.database.TransactionLogRepository;
import antifraud.exceptions.NoChangeException;
import antifraud.exceptions.NotFoundException;
import antifraud.exceptions.WrongFeedbackException;
import antifraud.models.CardLimit;
import antifraud.models.TransactionLog;
import antifraud.models.TransactionResponse;
import antifraud.models.TransactionStatus;
import antifraud.requests.TransactionFeedback;
import antifraud.requests.TransactionRequest;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionLogRepository transactionRepository;
    private final CardService cardService;
    private final CardLimitService cardLimitService;

    public TransactionService(TransactionLogRepository transactionRepository, CardService cardService,
                              CardLimitService cardLimitService) {
        this.transactionRepository = transactionRepository;
        this.cardService = cardService;
        this.cardLimitService = cardLimitService;
    }

    public TransactionResponse checkTransaction(TransactionRequest request, boolean validIp, boolean validCardNumber) {
        TransactionStatus transactionStatus = checkAmount(request.number(), request.amount());
        TransactionResponse response = new TransactionResponse();
        Set<String> errors = new TreeSet<>();

        if (transactionStatus == TransactionStatus.PROHIBITED) {
            errors.add("amount");
        }

        if (!validCardNumber) {
            errors.add("card-number");
        }
        if (!validIp) {
            errors.add("ip");
        }

        if (!validIp || !validCardNumber) {
            transactionStatus = TransactionStatus.PROHIBITED;
        }

        if (transactionStatus != TransactionStatus.ALLOWED && errors.isEmpty()) {
            errors.add("amount");
        }

        int regionsCount =
                transactionRepository.countByRegionInLastHour(request.date().minusHours(1L), request.date(),
                        request.region());
        int iPsCount =
                transactionRepository.countByIpInLastHour(request.date().minusHours(1L), request.date(), request.ip());

        if (regionsCount == 2 || iPsCount == 2) {
            transactionStatus = TransactionStatus.MANUAL_PROCESSING;
        } else if (regionsCount > 2 || iPsCount > 2) {
            transactionStatus = TransactionStatus.PROHIBITED;
        }

        if (regionsCount >= 2) {
            errors.add("region-correlation");
        }

        if (iPsCount >= 2) {
            errors.add("ip-correlation");
        }


        response.setResult(transactionStatus);
        if (!errors.isEmpty())
            response.setInfo(String.join(", ", errors));

        return response;
    }

    public void saveTransaction(TransactionRequest request, TransactionStatus transactionStatus) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        transactionRepository.save(new TransactionLog(request, transactionStatus, auth.getName()));
    }

    private TransactionStatus checkAmount(String cardNumber, Integer amount) {
        CardLimit cardLimit = cardLimitService.getCardLimit(cardNumber);
        TransactionStatus transactionStatus;
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount <= cardLimit.getMaxAllowed()) {
            transactionStatus = TransactionStatus.ALLOWED;
        } else if (amount > cardLimit.getMaxAllowed() && amount <= cardLimit.getManualMax()) {
            transactionStatus = TransactionStatus.MANUAL_PROCESSING;
        } else {
            transactionStatus = TransactionStatus.PROHIBITED;
        }
        return transactionStatus;
    }

    public TransactionLog updateTransaction(TransactionFeedback transactionFeedback) {
        TransactionLog transactionLog =
                transactionRepository.findById(transactionFeedback.transactionId())
                        .orElseThrow(() -> new NotFoundException(
                                "Transaction not found!"));
        if (transactionLog.getResult().equals(transactionFeedback.feedback().name()))
            throw new WrongFeedbackException();
        if (!transactionLog.getFeedback().isEmpty())
            throw new NoChangeException();
        cardLimitService.updateCardLimit(transactionLog, transactionFeedback.feedback());
        transactionLog.setFeedback(transactionFeedback.feedback().name());
        transactionRepository.save(transactionLog);
        return transactionLog;
    }

    public Iterable<TransactionLog> listTransactions() {
        return transactionRepository.findAll();
    }

    public List<TransactionLog> listTransactionsForCard(String number) {
        cardService.validateCardNumber(number);
        List<TransactionLog> transactionLogs =
                transactionRepository.findByNumber(number);

        if (transactionLogs.isEmpty())
            throw new NotFoundException("No Transactions found!");

        return transactionLogs;
    }
}
