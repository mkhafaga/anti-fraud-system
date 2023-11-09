package antifraud.api;

import antifraud.models.TransactionStatus;
import antifraud.services.TransactionService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/antifraud")
public class AntiFraudController {

    private final TransactionService transactionService;

    public AntiFraudController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<Map<String, String>> validateTransaction(@RequestBody Map<String, Integer> amountParam) {
        Integer amount = amountParam.get("amount");
        TransactionStatus transactionStatus = transactionService.checkTransaction(amount);
        return ResponseEntity.ok(Map.of("result", transactionStatus.name()));
    }

    @ExceptionHandler
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        return ResponseEntity.badRequest().build();
    }
}
