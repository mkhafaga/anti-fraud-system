package antifraud.services;

import antifraud.database.AppUserRepository;
import antifraud.exceptions.UserNotFoundException;
import antifraud.models.AppUser;
import antifraud.models.Registration;
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
}
