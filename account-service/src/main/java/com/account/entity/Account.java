package com.account.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String number;

    private BigDecimal balance;

    private Long clientId;

    private boolean active;

    private BigDecimal reservedMoney;

    private String cardNumber;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Account() {
    }

    public Account(String number, Long clientId) {

        this.number = number;
        this.balance = BigDecimal.ZERO;
        this.clientId = clientId;
        this.active = true;
        this.reservedMoney=BigDecimal.ZERO;
    }

    public Account(Long id) {
        this.id = id;
    }

    public BigDecimal getReservedMoney() {
        return reservedMoney;
    }

    public void setReservedMoney(BigDecimal reservedMoney) {
        this.reservedMoney = reservedMoney;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", balance=" + balance +
                ", clientId=" + clientId +
                ", active=" + active +
                ", reservedMoney=" + reservedMoney +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }
}
