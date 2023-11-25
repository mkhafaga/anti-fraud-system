package antifraud.api;

import antifraud.requests.CardRequest;
import antifraud.models.StolenCard;
import antifraud.services.CardService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/antifraud/stolencard")
public class StolenCardController {

    private final CardService cardService;

    public StolenCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<Iterable<StolenCard>> listCards() {
        return ResponseEntity.ok(cardService.getStolenCards());
    }

    @PostMapping
    public ResponseEntity<StolenCard> addCard(@RequestBody CardRequest request) {
        StolenCard card = cardService.addCard(request.number());
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<Map<String, String>> deleteCard(@PathVariable String number) {
        cardService.deleteCard(number);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "Card %s successfully removed!".formatted(number));
        return ResponseEntity.ok(response);
    }
}
