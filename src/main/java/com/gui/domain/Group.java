package com.gui.domain;

import java.util.ArrayList;

public class Group extends Entity<Long>{

    private ArrayList<Long> users;
    private String name;
    public Group(ArrayList<Long> users,String name) {
        this.users = users;
        this.name=name;
    }

    public ArrayList<Long> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public void setUsers(ArrayList<Long> users) {
        this.users = users;
    }
}
