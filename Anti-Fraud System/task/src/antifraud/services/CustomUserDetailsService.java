package antifraud.services;

import antifraud.database.AppUserRepository;
import antifraud.models.AppUser;
import antifraud.models.AppUserAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findAppUserByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return new AppUserAdapter(user);
    }
}
