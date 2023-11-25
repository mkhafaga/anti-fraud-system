package antifraud.api;

import antifraud.requests.IpRequest;
import antifraud.models.SuspiciousIp;
import antifraud.services.SuspiciousIpService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
public class SuspiciousIpController {

    private final SuspiciousIpService suspiciousIpService;

    public SuspiciousIpController(SuspiciousIpService suspiciousIpService) {
        this.suspiciousIpService = suspiciousIpService;
    }

    @GetMapping
    public ResponseEntity<Iterable<SuspiciousIp>> listIps() {
        return ResponseEntity.ok(suspiciousIpService.getIps());
    }

    @PostMapping
    public ResponseEntity<SuspiciousIp> addIp(@RequestBody IpRequest request) {
        SuspiciousIp suspiciousIp = suspiciousIpService.addIp(request.ip());
        return ResponseEntity.ok(suspiciousIp);
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity<Map<String, String>> deleteIp(@PathVariable String ip) {
        suspiciousIpService.deleteIp(ip);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "IP %s successfully removed!".formatted(ip));
        return ResponseEntity.ok(response);
    }
}
