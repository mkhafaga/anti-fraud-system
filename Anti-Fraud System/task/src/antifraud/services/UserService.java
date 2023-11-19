package antifraud.services;

import antifraud.database.AppUserRepository;
import antifraud.exceptions.NoChangeException;
import antifraud.exceptions.UserNotFoundException;
import antifraud.models.AppUser;
import antifraud.models.LockStatus;
import antifraud.models.Registration;
import antifraud.models.UserAccess;
import antifraud.models.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser registerUser(Registration registration) {
        var user = new AppUser();
        user.setName(registration.name());
        user.setUsername(registration.username());
        user.setPassword(passwordEncoder.encode(registration.password()));
        if (userRepository.count() == 0) {
            user.setRole("ADMINISTRATOR");
        } else {
            user.setRole("MERCHANT");
            user.setLocked(true);
        }
        userRepository.save(user);
        return user;
    }

    public Iterable<AppUser> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(String username) {
        AppUser user =
                userRepository.findAppUserByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }

    public AppUser updateRole(UserRole userRole) {
        AppUser user =
                userRepository.findAppUserByUsername(userRole.username())
                        .orElseThrow(() -> new UserNotFoundException("User not found"));
        String validRole = userRole.role();
        if (!(validRole.equals("MERCHANT") || validRole.equals("SUPPORT"))) {
            throw new IllegalArgumentException();
        }
        if(user.getRole().equals(userRole.role())){
            throw new NoChangeException();
        }
        user.setRole(validRole);
        return userRepository.save(user);
    }

    public void updateAccess(UserAccess userAccess) {
        AppUser user =
                userRepository.findAppUserByUsername(userAccess.username())
                        .orElseThrow(() -> new UserNotFoundException("User not found"));
        boolean locked = false;
        if (userAccess.operation() == LockStatus.LOCK) {
            locked = true;
        }
        user.setLocked(locked);
        userRepository.save(user);
    }
}
