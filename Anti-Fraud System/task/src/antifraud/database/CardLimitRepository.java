package antifraud.database;

import antifraud.models.CardLimit;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CardLimitRepository extends CrudRepository<CardLimit, Long> {
    Optional<CardLimit> findCardLimitByNumber(String number);
}
