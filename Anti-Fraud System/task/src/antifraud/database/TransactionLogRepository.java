package antifraud.database;

import antifraud.models.Region;
import antifraud.models.TransactionLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TransactionLogRepository extends CrudRepository<TransactionLog, Long> {
    @Query("select count(distinct t.ip) from TransactionLog t where t.date between ?1 and ?2 and t.ip != ?3")
    Integer countByIpInLastHour(LocalDateTime from, LocalDateTime to, String ip);

    @Query("select count(distinct t.region) from TransactionLog t where t.date between ?1 and ?2 and t.region != ?3")
    Integer countByRegionInLastHour(LocalDateTime from, LocalDateTime to, Region region);

    List<TransactionLog> findByNumber(String number);

}
