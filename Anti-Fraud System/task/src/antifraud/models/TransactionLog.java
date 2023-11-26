package antifraud.models;

import antifraud.requests.TransactionRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class TransactionLog {
    @Id
    @GeneratedValue
    @JsonProperty("transactionId")
    private long id;
    private int amount;
    private String ip;
    private String number;
    private Region region;
    private LocalDateTime date;
    @JsonIgnore
    private String username;

    private String result;
    private String feedback;

    public TransactionLog(TransactionRequest request, TransactionStatus transactionStatus, String username) {
        this.amount = request.amount();
        this.ip = request.ip();
        this.number = request.number();
        this.region = request.region();
        this.date = request.date();
        this.result = transactionStatus.name();
        this.feedback = "";
        this.username = username;
    }

    public TransactionLog() {

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
