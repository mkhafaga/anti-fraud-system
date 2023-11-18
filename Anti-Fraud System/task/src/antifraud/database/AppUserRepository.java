package antifraud.database;

import antifraud.models.AppUser;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByUsername(String username);

    Optional<AppUser> findAppUserByUsernameIgnoreCase(String username);
}
