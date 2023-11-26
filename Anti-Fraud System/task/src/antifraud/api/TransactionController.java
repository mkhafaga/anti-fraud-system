package antifraud.api;

import antifraud.models.TransactionLog;
import antifraud.models.TransactionStatus;
import antifraud.requests.TransactionFeedback;
import antifraud.requests.TransactionRequest;
import antifraud.models.TransactionResponse;
import antifraud.services.CardService;
import antifraud.services.SuspiciousIpService;
import antifraud.services.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    private final TransactionService transactionService;
    private final SuspiciousIpService suspiciousIpService;
    private final CardService cardService;

    public TransactionController(TransactionService transactionService, SuspiciousIpService suspiciousIpService,
                                 CardService cardService) {
        this.transactionService = transactionService;
        this.suspiciousIpService = suspiciousIpService;
        this.cardService = cardService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponse> validateTransaction(@RequestBody @Valid TransactionRequest request) {
        boolean validIp = suspiciousIpService.validateIp(request.ip());
        boolean validCardNumber = cardService.validateCardNumber(request.number());
        TransactionResponse response = transactionService.checkTransaction(request, validIp, validCardNumber);
        TransactionStatus transactionStatus = response.getResult();
        transactionService.saveTransaction(request, transactionStatus);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/transaction")
    public ResponseEntity<TransactionLog> updateTransaction(@RequestBody TransactionFeedback feedback) {
        TransactionLog transactionLog = transactionService.updateTransaction(feedback);
        return ResponseEntity.ok(transactionLog);
    }

    @GetMapping("/history")
    public ResponseEntity<Iterable<TransactionLog>> getTransactions() {
        Iterable<TransactionLog> transactionLogs = transactionService.listTransactions();
        return ResponseEntity.ok(transactionLogs);
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<TransactionLog>> getTransactionsForCard(@PathVariable String number) {
        List<TransactionLog> transactionLogs = transactionService.listTransactionsForCard(number);
        return ResponseEntity.ok(transactionLogs);
    }

    @ExceptionHandler
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        return ResponseEntity.badRequest().build();
    }
}
