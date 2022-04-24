package com.gui.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Define a Friendship type entity
 */
public class Friendship extends Entity<Tuple<Long, Long>> {

    String date;
    Long u1, u2;
    private String status;

    public Friendship(Long u1, Long u2) {
        Tuple id = new Tuple<Long, Long>(u1, u2);
        this.u1 = u1;
        this.u2 = u2;
        setId(id);
        var dat = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        date = dat.format(formatter);
        this.status = "Pending";
    }

    public Friendship(Long u1, Long u2, String date, String status) {
        Tuple id = new Tuple<Long, Long>(u1, u2);
        this.u1 = u1;
        this.u2 = u2;
        setId(id);
        this.date = date;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getU1() {
        return u1;
    }

    public Long getU2() {
        return u2;
    }

    /**
     * @return the date when the friendship was created
     */
    public String getDate() {
        return date;
    }

    public void setU1(Long u1) {
        this.u1 = u1;
    }

    public void setU2(Long u2) {
        this.u2 = u2;
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                "Id Utilizator1= " + u1 +
                ", Id Utilizator2= " + u2 +
                ", Ziua in care s-au imprietenit= " + date +
                ", Status= " + status +
                '}';
    }
}
