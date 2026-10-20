package com.esports.domain.model;

import java.math.BigDecimal;

public class ProPlayer extends Player {
    private BigDecimal salary;
    private String sponsor;

    public ProPlayer() {
        super();
    }

    public ProPlayer(Long id, String nickname, String role, BigDecimal salary, String sponsor) {
        super(id, nickname, role);
        this.salary = salary;
        this.sponsor = sponsor;
    }

    @Override
    public String getPlayerType() {
        return "PRO";
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
