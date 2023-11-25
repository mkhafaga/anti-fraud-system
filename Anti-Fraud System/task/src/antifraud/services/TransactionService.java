package antifraud.services;

import antifraud.database.TransactionLogRepository;
import antifraud.models.TransactionLog;
import antifraud.models.TransactionResponse;
import antifraud.models.TransactionStatus;
import antifraud.requests.TransactionRequest;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionLogRepository transactionRepository;

    public TransactionService(TransactionLogRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse checkTransaction(TransactionRequest request, boolean validIp, boolean validCardNumber) {
        TransactionStatus transactionStatus = checkAmount(request.amount());
        TransactionResponse response = new TransactionResponse();
        StringBuffer info = new StringBuffer();
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

    public void saveTransaction(TransactionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        transactionRepository.save(new TransactionLog(request, auth.getName()));
    }

    private TransactionStatus checkAmount(Integer amount) {
        TransactionStatus transactionStatus;
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount <= 200) {
            transactionStatus = TransactionStatus.ALLOWED;
        } else if (amount > 200 && amount <= 1500) {
            transactionStatus = TransactionStatus.MANUAL_PROCESSING;
        } else {
            transactionStatus = TransactionStatus.PROHIBITED;
        }
        return transactionStatus;
    }
}
