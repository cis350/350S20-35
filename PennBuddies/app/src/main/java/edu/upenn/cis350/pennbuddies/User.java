package edu.upenn.cis350.pennbuddies;

public class User {
    private String name;
    private String username;
    private String email;
    private String password;
    private String hair;
    private String eyes;
    private String height;
    private String weight;
    private String dob;
    private String phone;
    private String[] friends;

    public User(String name, String username, String email, String password, String hair, String eyes,
                  String height, String weight, String dob, String phone) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.height = height;
        this.eyes = eyes;
        this.hair = hair;
        this.weight = weight;
        this.dob = dob;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getHair() {
        return hair;
    }

    public String getEyes() {
        return eyes;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getDob() {
        return dob;
    }

    public String[] getFriends() {
        return friends;
    }

}
