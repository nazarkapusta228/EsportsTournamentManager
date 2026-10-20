package com.esports.domain.model;

public class AmateurPlayer extends Player {
    private String rank;
    private Integer hoursPlayed;

    public AmateurPlayer() {
        super();
    }

    public AmateurPlayer(Long id, String nickname, String role, String rank, Integer hoursPlayed) {
        super(id, nickname, role);
        this.rank = rank;
        this.hoursPlayed = hoursPlayed;
    }

    @Override
    public String getPlayerType() {
        return "AMATEUR";
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getHoursPlayed() {
        return hoursPlayed;
    }

    public void setHoursPlayed(Integer hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }
}
