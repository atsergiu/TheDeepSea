package com.gui.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Define an User type entity
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String password;
    private List<User> friends;

    /**
     * Constructor for user
     */

    public User(String firstName, String lastName, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.friends = new ArrayList<>();
    }

    public User(String firstName, String lastName, String gender, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.password = password;
    }
    public void setEmail(String email) {this.email = email;}

    public void setPassword(String password) {this.password = password;}

    public String getEmail() {return email;}

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "Id='" + getId() + '\'' +
                "Prenume='" + firstName + '\'' +
                ", Nume='" + lastName + '\'' +
                ", Gen='" + gender + '\'' +
                '}';
    }

    public String getFullName() {
        return firstName+" "+lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getId());
    }
}