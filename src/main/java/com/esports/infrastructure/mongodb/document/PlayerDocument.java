package com.esports.infrastructure.mongodb.document;

import java.math.BigDecimal;
import org.bson.codecs.pojo.annotations.BsonId;

/** MongoDB representation of both professional and amateur players. */
public class PlayerDocument implements IdentifiedDocument {
    @BsonId
    private Long id;
    private String type;
    private String nickname;
    private String role;
    private BigDecimal salary;
    private String sponsor;
    private String rank;
    private Integer hoursPlayed;

    public PlayerDocument() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public String getSponsor() { return sponsor; }
    public void setSponsor(String sponsor) { this.sponsor = sponsor; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public Integer getHoursPlayed() { return hoursPlayed; }
    public void setHoursPlayed(Integer hoursPlayed) { this.hoursPlayed = hoursPlayed; }
}
