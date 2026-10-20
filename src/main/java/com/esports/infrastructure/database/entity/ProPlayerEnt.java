package com.esports.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PRO")
public class ProPlayerEnt extends PlayerEnt {

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(length = 100)
    private String sponsor;

    public ProPlayerEnt() {
        super();
    }

    public ProPlayerEnt(String nickname, String role, BigDecimal salary, String sponsor) {
        super(nickname, role);
        this.salary = salary;
        this.sponsor = sponsor;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }
}
