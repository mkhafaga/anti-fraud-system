package antifraud.database;

import antifraud.models.StolenCard;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    boolean existsByNumber(String number);

    Optional<StolenCard> findByNumber(String number);
}
