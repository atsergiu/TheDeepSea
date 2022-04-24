package com.gui.domain;

public class FriendList {
    private String firstName;
    private String lastName;
    private String date;
    private Long userId;
    private String gender;
    private String status;
    private String fullName;

    public FriendList(Long userId, String firstName, String lastName, String date) {
        this.userId=userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.fullName=firstName+" "+lastName;
    }

    public FriendList(String firstName, String lastName, String date, Long userId, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.userId = userId;
        this.status = status;
        this.fullName=firstName+" "+lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public FriendList(Long userId, String firstName, String lastName, String date, String gender) {
        this.userId=userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.gender=gender;
        this.fullName=firstName+" "+lastName;
    }


    public String getStatus() {return status;}

    public String getGender() {return gender;}

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDate() {
        return date;
    }
}
