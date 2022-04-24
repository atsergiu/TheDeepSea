package com.gui.domain;

import java.util.ArrayList;
import java.util.List;

public class Event extends Entity<Long> {
    String name;
    String description;
    String location;
    String date;
    Long creator;
    ArrayList<Long> participants;

    public Event(Long creator, String name, String description, String location, String date, ArrayList<Long> participants) {
        this.creator = creator;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public Long getCreator() {
        return creator;
    }

    public List<Long> getParticipants() {
        return participants;
    }
}
