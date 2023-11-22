package antifraud.services;

import antifraud.database.StolenCardRepository;
import antifraud.exceptions.NotFoundException;
import antifraud.models.StolenCard;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final StolenCardRepository stolenCardRepository;

    public CardService(StolenCardRepository stolenCardRepository) {
        this.stolenCardRepository = stolenCardRepository;
    }

    public StolenCard addCard(String cardNumber) {
        validateCardNumber(cardNumber);
        StolenCard stolenCard = new StolenCard();
        stolenCard.setNumber(cardNumber);
        return stolenCardRepository.save(stolenCard);
    }

    @Transactional
    public void deleteCard(String number) {
        validateCardNumber(number);
        StolenCard card = stolenCardRepository.findByNumber(number).
                orElseThrow(() -> new NotFoundException("Card not found!"));
        stolenCardRepository.delete(card);
    }

    public Iterable<StolenCard> getStolenCards() {
        return stolenCardRepository.findAll();
    }

    public boolean validateCardNumber(String number) {
        if (!isLuhnCompliant(number))
            throw new IllegalArgumentException();
        return !stolenCardRepository.existsByNumber(number);

    }

    private boolean isLuhnCompliant(String number) {
        if (number.length() < 16)
            return false;
        int checksum = Character.getNumericValue(number.charAt(number.length() - 1));
        int total = 0;

        for (int i = number.length() - 2; i >= 0; i--) {
            int sum = 0;
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
            }

            sum = digit / 10 + digit % 10;
            total += sum;
        }

        return 10 - total % 10 == checksum;
    }
}
