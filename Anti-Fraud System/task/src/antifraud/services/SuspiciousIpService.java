package antifraud.services;

import antifraud.database.SuspiciousIpRepository;
import antifraud.exceptions.NotFoundException;
import antifraud.models.SuspiciousIp;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class SuspiciousIpService {
    private final SuspiciousIpRepository suspiciousIpRepository;

    public SuspiciousIpService(SuspiciousIpRepository suspiciousIpRepository) {
        this.suspiciousIpRepository = suspiciousIpRepository;
    }

    public SuspiciousIp addIp(String ip) {
        validateIp(ip);
        SuspiciousIp suspiciousIp = new SuspiciousIp();
        suspiciousIp.setIpAddress(ip);
        return suspiciousIpRepository.save(suspiciousIp);
    }

    @Transactional
    public void deleteIp(String ipAddress) {
        validateIp(ipAddress);
        SuspiciousIp suspiciousIp =
                suspiciousIpRepository.findByIpAddress(ipAddress).
                        orElseThrow(() -> new NotFoundException("IP not found!"));
        suspiciousIpRepository.delete(suspiciousIp);
    }

    public Iterable<SuspiciousIp> getIps() {
        return suspiciousIpRepository.findAll();
    }

    public boolean validateIp(String ipAddress) {
        boolean blacklisted = suspiciousIpRepository.existsByIpAddress(ipAddress);
        String[] ipParts = ipAddress.split("\\.");
        if (ipParts.length < 4)
            throw new IllegalArgumentException();
        boolean validIp = Arrays.stream(ipParts).map(Integer::parseInt)
                .allMatch((number) -> number >= 0 && number <= 255);
        if (!validIp)
            throw new IllegalArgumentException();
        return !blacklisted && validIp;
    }
}
