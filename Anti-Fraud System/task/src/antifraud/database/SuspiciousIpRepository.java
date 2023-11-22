package antifraud.database;

import antifraud.models.SuspiciousIp;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface SuspiciousIpRepository extends CrudRepository<SuspiciousIp, Long> {

    Optional<SuspiciousIp> findByIpAddress(String ipAddress);
    void removeByIpAddress(String ipAddress);

    boolean existsByIpAddress(String ipAddress);
}
