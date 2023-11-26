package antifraud.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class CardLimit {
    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private String number;
    private int maxAllowed;
    private int manualMax;

    public CardLimit(String number, int maxAllowed, int manualMax) {
        this.number = number;
        this.maxAllowed = maxAllowed;
        this.manualMax = manualMax;
    }

    public CardLimit() {

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(int maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public int getManualMax() {
        return manualMax;
    }

    public void setManualMax(int manualMax) {
        this.manualMax = manualMax;
    }
}
